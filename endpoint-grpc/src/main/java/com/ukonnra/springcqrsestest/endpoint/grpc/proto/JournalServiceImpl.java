package com.ukonnra.springcqrsestest.endpoint.grpc.proto;

import com.google.protobuf.Value;
import com.ukonnra.springcqrsestest.shared.journal.JournalCommand;
import com.ukonnra.springcqrsestest.shared.journal.JournalPresentation;
import com.ukonnra.springcqrsestest.shared.journal.JournalQuery;
import com.ukonnra.springcqrsestest.shared.journal.JournalService;
import com.ukonnra.springcqrsestest.shared.user.UserRepository;
import com.ukonnra.springcqrsestest.shared.user.UserService;
import io.grpc.stub.StreamObserver;
import java.util.HashSet;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.devh.boot.grpc.server.service.GrpcService;

@Getter
@GrpcService
@AllArgsConstructor
public class JournalServiceImpl extends JournalServiceGrpc.JournalServiceImplBase
    implements EndpointGrpcService {
  private final JournalService journalService;
  private final UserService userService;
  private final UserRepository userRepository;

  @Override
  public void findOne(
      JournalProto.JournalServiceFindOneRequest request,
      StreamObserver<JournalProto.JournalServiceFindOneResponse> responseObserver) {
    final var operator = this.getOperator();
    final var result =
        this.journalService.findOne(operator, this.convertFromProto(request.getQuery()));

    if (result.isPresent()) {
      responseObserver.onNext(
          JournalProto.JournalServiceFindOneResponse.newBuilder()
              .setValue(this.convertToProto(result.get()))
              .build());
    } else {
      responseObserver.onNext(JournalProto.JournalServiceFindOneResponse.getDefaultInstance());
    }
    responseObserver.onCompleted();
  }

  @Override
  public void findAll(
      JournalProto.JournalServiceFindAllRequest request,
      StreamObserver<JournalProto.JournalServiceFindAllResponse> responseObserver) {
    final var operator = this.getOperator();
    final var results =
        this.journalService
            .findAll(operator, this.convertFromProto(request.getQuery()), request.getSize())
            .stream()
            .map(this::convertToProto)
            .toList();
    responseObserver.onNext(
        JournalProto.JournalServiceFindAllResponse.newBuilder().addAllValues(results).build());
    responseObserver.onCompleted();
  }

  @Override
  public void handleCommand(
      JournalProto.JournalServiceHandleCommandRequest request,
      StreamObserver<JournalProto.JournalServiceHandleCommandResponse> responseObserver) {
    final var operator = this.getOperator();
    this.journalService.handleCommand(operator, this.convertFromProto(request));
    responseObserver.onNext(JournalProto.JournalServiceHandleCommandResponse.getDefaultInstance());
    responseObserver.onCompleted();
  }

  private JournalCommand convertFromProto(
      final JournalProto.JournalServiceHandleCommandRequest proto) {
    if (proto.hasCreate()) {
      return this.convertFromProto(proto.getCreate());
    } else if (proto.hasUpdate()) {
      return this.convertFromProto(proto.getUpdate());
    } else if (proto.hasDelete()) {
      return this.convertFromProto(proto.getDelete());
    } else {
      return this.convertFromProto(proto.getBatch());
    }
  }

  private JournalCommand.Create convertFromProto(final JournalProto.JournalCommandCreate proto) {
    return new JournalCommand.Create(
        proto.getName(),
        proto.getAdminsList().stream().map(UUID::fromString).collect(Collectors.toSet()),
        proto.getMembersList().stream().map(UUID::fromString).collect(Collectors.toSet()),
        new HashSet<>(proto.getTagsList()));
  }

  private JournalCommand.Update convertFromProto(final JournalProto.JournalCommandUpdate proto) {
    return new JournalCommand.Update(
        UUID.fromString(proto.getId()),
        proto.getName(),
        proto.getAdminsList().stream().map(UUID::fromString).collect(Collectors.toSet()),
        proto.hasMembers()
            ? proto.getMembers().getValuesList().stream()
                .filter(Value::hasStringValue)
                .map(v -> UUID.fromString(v.getStringValue()))
                .collect(Collectors.toSet())
            : null,
        proto.hasTags()
            ? proto.getTags().getValuesList().stream()
                .filter(Value::hasStringValue)
                .map(Value::getStringValue)
                .collect(Collectors.toSet())
            : null);
  }

  private JournalCommand.Delete convertFromProto(final JournalProto.JournalCommandDelete proto) {
    return new JournalCommand.Delete(UUID.fromString(proto.getId()));
  }

  private JournalCommand.Batch convertFromProto(final JournalProto.JournalCommandBatch proto) {
    return new JournalCommand.Batch(
        proto.getCreateList().stream().map(this::convertFromProto).collect(Collectors.toSet()),
        proto.getUpdateList().stream().map(this::convertFromProto).collect(Collectors.toSet()),
        proto.getDeleteList().stream().map(UUID::fromString).collect(Collectors.toSet()));
  }

  private JournalQuery convertFromProto(final JournalProto.JournalQuery proto) {
    return new JournalQuery(
        proto.getIdList().stream().map(UUID::fromString).collect(Collectors.toSet()),
        proto.getAdminIdList().stream().map(UUID::fromString).collect(Collectors.toSet()),
        proto.getMemberIdList().stream().map(UUID::fromString).collect(Collectors.toSet()),
        new HashSet<>(proto.getTagList()),
        proto.getFullText());
  }

  private JournalProto.Journal convertToProto(final JournalPresentation model) {
    final var builder =
        JournalProto.Journal.newBuilder()
            .setId(model.id().toString())
            .setCreatedDate(EndpointGrpcService.convertToProto(model.createdDate()))
            .setVersion(model.version())
            .setName(model.name())
            .addAllAdmins(model.admins().stream().map(UUID::toString).toList())
            .addAllMembers(model.members().stream().map(UUID::toString).toList())
            .addAllTags(model.tags())
            .setPermission(EndpointGrpcService.convertToProto(model.permission()));

    if (model.deletedDate() != null) {
      builder.setDeletedDate(EndpointGrpcService.convertToProto(model.deletedDate()));
    }

    return builder.build();
  }
}
