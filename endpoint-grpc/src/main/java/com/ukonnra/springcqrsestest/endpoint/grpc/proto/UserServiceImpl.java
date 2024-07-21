package com.ukonnra.springcqrsestest.endpoint.grpc.proto;

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
    implements GrpcServiceMixin {
  private final UserService userService;
  private final UserRepository userRepository;

  @Override
  public void findOne(
      UserProto.FindOneRequest request,
      StreamObserver<UserProto.FindOneResponse> responseObserver) {
    final var operator = this.getOperator();

    final var result =
        this.userService.findOne(operator, this.convertFromProto(request.getQuery()));
    if (result.isPresent()) {
      responseObserver.onNext(
          UserProto.FindOneResponse.newBuilder()
              .setValue(this.convertToProto(result.get()))
              .build());
    } else {
      responseObserver.onNext(UserProto.FindOneResponse.getDefaultInstance());
    }
    responseObserver.onCompleted();
  }

  @Override
  public void findAll(
      UserProto.FindAllRequest request,
      StreamObserver<UserProto.FindAllResponse> responseObserver) {
    final var operator = this.getOperator();

    final var results =
        this.userService
            .findAll(operator, this.convertFromProto(request.getQuery()), request.getSize())
            .stream()
            .map(this::convertToProto)
            .toList();

    responseObserver.onNext(UserProto.FindAllResponse.newBuilder().addAllValues(results).build());

    responseObserver.onCompleted();
  }

  @Override
  public void handleCommand(
      UserProto.HandleCommandRequest request,
      StreamObserver<SharedProto.HandleCommandResponse> responseObserver) {
    super.handleCommand(request, responseObserver);
  }

  private UserPresentation convertFromProto(UserProto.User proto) {
    return new UserPresentation(
        UUID.fromString(proto.getId()),
        this.convertFromProto(proto.getCreatedDate()),
        proto.getVersion(),
        proto.hasDeletedDate() ? this.convertFromProto(proto.getDeletedDate()) : null,
        proto.getLoginName(),
        proto.getDisplayName(),
        proto.getSystemAdmin(),
        this.convertFromProto(proto.getPermission()));
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
            .setCreatedDate(this.convertToProto(model.createdDate()))
            .setVersion(model.version())
            .setLoginName(model.loginName())
            .setDisplayName(model.displayName())
            .setSystemAdmin(model.systemAdmin())
            .setPermission(this.convertToProto(model.permission()));

    if (model.deletedDate() != null) {
      builder.setDeletedDate(this.convertToProto(model.deletedDate()));
    }

    return builder.build();
  }
}
