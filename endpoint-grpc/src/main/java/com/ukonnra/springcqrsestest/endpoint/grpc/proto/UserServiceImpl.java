package com.ukonnra.springcqrsestest.endpoint.grpc.proto;

import com.ukonnra.springcqrsestest.shared.user.UserCommand;
import com.ukonnra.springcqrsestest.shared.user.UserPresentation;
import com.ukonnra.springcqrsestest.shared.user.UserQuery;
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
public class UserServiceImpl extends UserServiceGrpc.UserServiceImplBase
    implements EndpointGrpcService {
  private final UserService userService;
  private final UserRepository userRepository;

  @Override
  public void findOne(
      UserProto.UserServiceFindOneRequest request,
      StreamObserver<UserProto.UserServiceFindOneResponse> responseObserver) {
    final var operator = this.getOperator();

    final var result =
        this.userService.findOne(operator, this.convertFromProto(request.getQuery()));
    if (result.isPresent()) {
      responseObserver.onNext(
          UserProto.UserServiceFindOneResponse.newBuilder()
              .setValue(this.convertToProto(result.get()))
              .build());
    } else {
      responseObserver.onNext(UserProto.UserServiceFindOneResponse.getDefaultInstance());
    }
    responseObserver.onCompleted();
  }

  @Override
  public void findAll(
      UserProto.UserServiceFindAllRequest request,
      StreamObserver<UserProto.UserServiceFindAllResponse> responseObserver) {
    final var operator = this.getOperator();

    final var results =
        this.userService
            .findAll(operator, this.convertFromProto(request.getQuery()), request.getSize())
            .stream()
            .map(this::convertToProto)
            .toList();

    responseObserver.onNext(
        UserProto.UserServiceFindAllResponse.newBuilder().addAllValues(results).build());

    responseObserver.onCompleted();
  }

  @Override
  public void handleCommand(
      UserProto.UserServiceHandleCommandRequest request,
      StreamObserver<UserProto.UserServiceHandleCommandResponse> responseObserver) {
    final var operator = this.getOperator();

    final UserCommand command;
    if (request.hasCreate()) {
      command = this.convertFromProto(request.getCreate());
    } else if (request.hasUpdate()) {
      command = this.convertFromProto(request.getUpdate());
    } else if (request.hasDelete()) {
      command = this.convertFromProto(request.getDelete());
    } else {
      command = this.convertFromProto(request.getBatch());
    }

    this.userService.handleCommand(operator, command);

    responseObserver.onNext(UserProto.UserServiceHandleCommandResponse.getDefaultInstance());
    responseObserver.onCompleted();
  }

  private UserCommand.Create convertFromProto(final UserProto.UserCommandCreate proto) {
    return new UserCommand.Create(
        proto.getLoginName(), proto.getDisplayName(), proto.getSystemAdmin());
  }

  private UserCommand.Update convertFromProto(final UserProto.UserCommandUpdate proto) {
    return new UserCommand.Update(
        UUID.fromString(proto.getId()),
        proto.getLoginName(),
        proto.getDisplayName(),
        proto.hasSystemAdmin() ? proto.getSystemAdmin().getValue() : null);
  }

  private UserCommand.Delete convertFromProto(final UserProto.UserCommandDelete proto) {
    return new UserCommand.Delete(UUID.fromString(proto.getId()));
  }

  private UserCommand.Batch convertFromProto(final UserProto.UserCommandBatch proto) {
    return new UserCommand.Batch(
        proto.getCreateList().stream().map(this::convertFromProto).collect(Collectors.toSet()),
        proto.getUpdateList().stream().map(this::convertFromProto).collect(Collectors.toSet()),
        proto.getDeleteList().stream().map(UUID::fromString).collect(Collectors.toSet()));
  }

  private UserQuery convertFromProto(final UserProto.UserQuery proto) {
    return new UserQuery(
        proto.getIdList().stream().map(UUID::fromString).collect(Collectors.toSet()),
        new HashSet<>(proto.getLoginNameList()),
        proto.getFullText());
  }

  private UserProto.User convertToProto(UserPresentation model) {
    final var builder =
        UserProto.User.newBuilder()
            .setId(model.id().toString())
            .setCreatedDate(EndpointGrpcService.convertToProto(model.createdDate()))
            .setVersion(model.version())
            .setLoginName(model.loginName())
            .setDisplayName(model.displayName())
            .setSystemAdmin(model.systemAdmin())
            .setPermission(EndpointGrpcService.convertToProto(model.permission()));

    if (model.deletedDate() != null) {
      builder.setDeletedDate(EndpointGrpcService.convertToProto(model.deletedDate()));
    }

    return builder.build();
  }
}
