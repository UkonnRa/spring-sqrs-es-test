package com.ukonnra.springcqrsestest.endpoint.grpc;

import com.google.protobuf.BoolValue;
import com.ukonnra.springcqrsestest.endpoint.grpc.proto.EndpointGrpcService;
import com.ukonnra.springcqrsestest.endpoint.grpc.proto.UserProto;
import com.ukonnra.springcqrsestest.endpoint.grpc.proto.UserServiceGrpc;
import com.ukonnra.springcqrsestest.endpoint.grpc.proto.UserServiceImpl;
import com.ukonnra.springcqrsestest.shared.EventRepository;
import com.ukonnra.springcqrsestest.shared.user.UserCommand;
import com.ukonnra.springcqrsestest.shared.user.UserPresentation;
import com.ukonnra.springcqrsestest.shared.user.UserQuery;
import com.ukonnra.springcqrsestest.shared.user.UserRepository;
import com.ukonnra.springcqrsestest.testsuite.UserTest;
import com.ukonnra.springcqrsestest.testsuite.UserTestClient;
import java.util.Collection;
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
    classes = {EndpointGrpcTestConfiguration.class, UserServiceImpl.class},
    properties = {
      "grpc.server.inProcessName=user",
      "grpc.server.port=-1",
      "grpc.client.userService.address=in-process:user"
    })
public class EndpointGrpcUserTest implements UserTest {
  private final EventRepository eventRepository;
  private final ClientImpl userTestClient;
  private final UserRepository userRepository;

  @Service
  @AllArgsConstructor
  @Slf4j
  public static class ClientImpl implements UserTestClient {
    private final UserServiceGrpc.UserServiceBlockingStub userServiceStub;

    private UserServiceGrpc.UserServiceBlockingStub createStub(@Nullable UUID operatorId) {
      final UserServiceGrpc.UserServiceBlockingStub result;
      if (operatorId == null) {
        result = this.userServiceStub;
      } else {
        result =
            this.userServiceStub.withCallCredentials(
                CallCredentialsHelper.basicAuth(operatorId.toString(), "password"));
      }
      return result.withCompression("gzip");
    }

    @Override
    public Set<UserPresentation> findAllByIds(@Nullable UUID operatorId, Collection<UUID> ids) {
      log.info("Find By Ids: {}", ids);
      final var resp =
          this.createStub(operatorId)
              .findAll(
                  UserProto.UserServiceFindAllRequest.newBuilder()
                      .setQuery(
                          UserProto.UserQuery.newBuilder()
                              .addAllId(ids.stream().map(UUID::toString).toList())
                              .build())
                      .build());
      return resp.getValuesList().stream().map(this::convertFromProto).collect(Collectors.toSet());
    }

    @Override
    public Set<UserPresentation> findAll(
        @Nullable UUID operatorId, UserQuery query, @Nullable Integer size) {
      log.info("Find All: {}", query);
      final var queryProto =
          UserProto.UserServiceFindAllRequest.newBuilder().setQuery(this.convertToProto(query));
      if (size != null && size > 0) {
        queryProto.setSize(size);
      }

      final var resp = this.createStub(operatorId).findAll(queryProto.build());
      return resp.getValuesList().stream().map(this::convertFromProto).collect(Collectors.toSet());
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void handleCommand(@Nullable UUID operatorId, UserCommand command) {
      log.info("Handle Command: {}", command);
      this.createStub(operatorId).handleCommand(this.convertToProto(command));
    }

    private UserPresentation convertFromProto(UserProto.User proto) {
      return new UserPresentation(
          UUID.fromString(proto.getId()),
          EndpointGrpcService.convertFromProto(proto.getCreatedDate()),
          proto.getVersion(),
          proto.hasDeletedDate()
              ? EndpointGrpcService.convertFromProto(proto.getDeletedDate())
              : null,
          proto.getLoginName(),
          proto.getDisplayName(),
          proto.getSystemAdmin(),
          EndpointGrpcService.convertFromProto(proto.getPermission()));
    }

    private UserProto.UserQuery convertToProto(final UserQuery query) {
      return UserProto.UserQuery.newBuilder()
          .addAllId(query.id().stream().map(UUID::toString).toList())
          .addAllLoginName(query.loginName())
          .setFullText(query.fullText())
          .build();
    }

    private UserProto.UserServiceHandleCommandRequest convertToProto(final UserCommand command) {
      final var builder = UserProto.UserServiceHandleCommandRequest.newBuilder();
      return switch (command) {
        case UserCommand.Create create -> builder.setCreate(this.convertToProto(create)).build();
        case UserCommand.Update update -> builder.setUpdate(this.convertToProto(update)).build();
        case UserCommand.Delete delete ->
            builder
                .setDelete(
                    UserProto.UserCommandDelete.newBuilder().setId(delete.id().toString()).build())
                .build();
        case UserCommand.Batch batch -> builder.setBatch(this.convertToProto(batch)).build();
      };
    }

    private UserProto.UserCommandCreate convertToProto(final UserCommand.Create command) {
      return UserProto.UserCommandCreate.newBuilder()
          .setLoginName(command.loginName())
          .setDisplayName(command.displayName())
          .setSystemAdmin(command.systemAdmin())
          .build();
    }

    private UserProto.UserCommandUpdate convertToProto(final UserCommand.Update command) {
      final var builder =
          UserProto.UserCommandUpdate.newBuilder()
              .setId(command.id().toString())
              .setLoginName(command.loginName())
              .setDisplayName(command.displayName());

      if (command.systemAdmin() != null) {
        builder.setSystemAdmin(BoolValue.of(command.systemAdmin()));
      }

      return builder.build();
    }

    private UserProto.UserCommandBatch convertToProto(final UserCommand.Batch command) {
      return UserProto.UserCommandBatch.newBuilder()
          .addAllCreate(command.create().stream().map(this::convertToProto).toList())
          .addAllUpdate(command.update().stream().map(this::convertToProto).toList())
          .addAllDelete(command.delete().stream().map(UUID::toString).toList())
          .build();
    }
  }
}
