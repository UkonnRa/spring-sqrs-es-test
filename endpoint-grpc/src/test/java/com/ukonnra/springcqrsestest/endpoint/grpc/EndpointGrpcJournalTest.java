package com.ukonnra.springcqrsestest.endpoint.grpc;

import com.google.protobuf.ListValue;
import com.google.protobuf.Value;
import com.ukonnra.springcqrsestest.endpoint.grpc.proto.EndpointGrpcService;
import com.ukonnra.springcqrsestest.endpoint.grpc.proto.JournalProto;
import com.ukonnra.springcqrsestest.endpoint.grpc.proto.JournalServiceGrpc;
import com.ukonnra.springcqrsestest.endpoint.grpc.proto.JournalServiceImpl;
import com.ukonnra.springcqrsestest.shared.EventRepository;
import com.ukonnra.springcqrsestest.shared.journal.JournalCommand;
import com.ukonnra.springcqrsestest.shared.journal.JournalPresentation;
import com.ukonnra.springcqrsestest.shared.journal.JournalQuery;
import com.ukonnra.springcqrsestest.shared.journal.JournalRepository;
import com.ukonnra.springcqrsestest.shared.user.UserRepository;
import com.ukonnra.springcqrsestest.testsuite.JournalTest;
import com.ukonnra.springcqrsestest.testsuite.JournalTestClient;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.security.CallCredentialsHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

@Getter
@AllArgsConstructor(onConstructor = @__(@Autowired))
@SpringBootTest(
    classes = {EndpointGrpcTestConfiguration.class, JournalServiceImpl.class},
    properties = {
      "grpc.server.inProcessName=journal",
      "grpc.server.port=-1",
      "grpc.client.journalService.address=in-process:journal"
    })
public class EndpointGrpcJournalTest implements JournalTest {
  private final EventRepository eventRepository;
  private final ClientImpl journalTestClient;
  private final UserRepository userRepository;
  private final JournalRepository journalRepository;

  @Service
  @AllArgsConstructor
  @Slf4j
  public static class ClientImpl implements JournalTestClient {
    private final JournalServiceGrpc.JournalServiceBlockingStub journalServiceStub;

    private JournalServiceGrpc.JournalServiceBlockingStub createStub(@Nullable UUID operatorId) {
      final JournalServiceGrpc.JournalServiceBlockingStub result;
      if (operatorId == null) {
        result = this.journalServiceStub;
      } else {
        result =
            this.journalServiceStub.withCallCredentials(
                CallCredentialsHelper.basicAuth(operatorId.toString(), "password"));
      }
      return result.withCompression("gzip");
    }

    @Override
    public Set<JournalPresentation> findAllByIds(@Nullable UUID operatorId, Collection<UUID> ids) {
      log.info("Find By Ids: {}", ids);
      final var resp =
          this.createStub(operatorId)
              .findAll(
                  JournalProto.JournalServiceFindAllRequest.newBuilder()
                      .setQuery(
                          JournalProto.JournalQuery.newBuilder()
                              .addAllId(ids.stream().map(UUID::toString).toList())
                              .build())
                      .build());
      return resp.getValuesList().stream().map(this::convertFromProto).collect(Collectors.toSet());
    }

    @Override
    public Set<JournalPresentation> findAll(
        @Nullable UUID operatorId, JournalQuery query, @Nullable Integer size) {
      log.info("Find All: {}", query);
      final var queryProto =
          JournalProto.JournalServiceFindAllRequest.newBuilder()
              .setQuery(this.convertToProto(query));
      if (size != null && size > 0) {
        queryProto.setSize(size);
      }
      final var resp = this.createStub(operatorId).findAll(queryProto.build());
      return resp.getValuesList().stream().map(this::convertFromProto).collect(Collectors.toSet());
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void handleCommand(@Nullable UUID operatorId, JournalCommand command) {
      log.info("Handle Command: {}", command);
      this.createStub(operatorId).handleCommand(this.convertToProto(command));
    }

    private JournalPresentation convertFromProto(JournalProto.Journal proto) {
      return new JournalPresentation(
          UUID.fromString(proto.getId()),
          EndpointGrpcService.convertFromProto(proto.getCreatedDate()),
          proto.getVersion(),
          proto.hasDeletedDate()
              ? EndpointGrpcService.convertFromProto(proto.getDeletedDate())
              : null,
          proto.getName(),
          proto.getAdminsList().stream().map(UUID::fromString).collect(Collectors.toSet()),
          proto.getMembersList().stream().map(UUID::fromString).collect(Collectors.toSet()),
          new HashSet<>(proto.getTagsList()),
          EndpointGrpcService.convertFromProto(proto.getPermission()));
    }

    private JournalProto.JournalQuery convertToProto(final JournalQuery query) {
      return JournalProto.JournalQuery.newBuilder()
          .addAllId(query.id().stream().map(UUID::toString).toList())
          .addAllAdminId(query.adminId().stream().map(UUID::toString).toList())
          .addAllMemberId(query.memberId().stream().map(UUID::toString).toList())
          .addAllTag(query.tag())
          .setFullText(query.fullText())
          .build();
    }

    private JournalProto.JournalServiceHandleCommandRequest convertToProto(
        final JournalCommand command) {
      final var builder = JournalProto.JournalServiceHandleCommandRequest.newBuilder();
      return switch (command) {
        case JournalCommand.Create create -> builder.setCreate(this.convertToProto(create)).build();
        case JournalCommand.Update update -> builder.setUpdate(this.convertToProto(update)).build();
        case JournalCommand.Delete delete ->
            builder
                .setDelete(
                    JournalProto.JournalCommandDelete.newBuilder()
                        .setId(delete.id().toString())
                        .build())
                .build();
        case JournalCommand.Batch batch -> builder.setBatch(this.convertToProto(batch)).build();
      };
    }

    private JournalProto.JournalCommandCreate convertToProto(final JournalCommand.Create command) {
      return JournalProto.JournalCommandCreate.newBuilder()
          .setName(command.name())
          .addAllAdmins(command.admins().stream().map(UUID::toString).toList())
          .addAllMembers(command.members().stream().map(UUID::toString).toList())
          .addAllTags(command.tags())
          .build();
    }

    private JournalProto.JournalCommandUpdate convertToProto(final JournalCommand.Update command) {
      final var builder =
          JournalProto.JournalCommandUpdate.newBuilder()
              .setId(command.id().toString())
              .setName(command.name())
              .addAllAdmins(command.admins().stream().map(UUID::toString).toList());

      if (command.members() != null) {
        builder.setMembers(
            ListValue.newBuilder()
                .addAllValues(
                    command.members().stream()
                        .map(id -> Value.newBuilder().setStringValue(id.toString()).build())
                        .toList())
                .build());
      }

      if (command.tags() != null) {
        builder.setTags(
            ListValue.newBuilder()
                .addAllValues(
                    command.tags().stream()
                        .map(tag -> Value.newBuilder().setStringValue(tag).build())
                        .toList())
                .build());
      }

      return builder.build();
    }

    private JournalProto.JournalCommandBatch convertToProto(final JournalCommand.Batch command) {
      return JournalProto.JournalCommandBatch.newBuilder()
          .addAllCreate(command.create().stream().map(this::convertToProto).toList())
          .addAllUpdate(command.update().stream().map(this::convertToProto).toList())
          .addAllDelete(command.delete().stream().map(UUID::toString).toList())
          .build();
    }
  }
}
