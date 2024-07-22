package com.ukonnra.springcqrsestest.endpoint.grpc.proto;

import com.google.protobuf.Timestamp;
import com.ukonnra.springcqrsestest.shared.Permission;
import com.ukonnra.springcqrsestest.shared.user.User;
import com.ukonnra.springcqrsestest.shared.user.UserRepository;
import java.time.Instant;
import java.util.UUID;
import org.springframework.lang.Nullable;
import org.springframework.security.core.context.SecurityContextHolder;

public interface EndpointGrpcService {
  UserRepository getUserRepository();

  default @Nullable User getOperator() {
    final var authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
      return null;
    }

    try {
      final var id = UUID.fromString(authentication.getPrincipal().toString());
      return this.getUserRepository().findById(id).orElse(null);
    } catch (IllegalArgumentException e) {
      return null;
    }
  }

  static Instant convertFromProto(Timestamp proto) {
    return Instant.ofEpochSecond(proto.getSeconds(), proto.getNanos());
  }

  static Permission convertFromProto(SharedProto.Permission proto) {
    if (proto.hasValue()) {
      return new Permission(proto.getValue().getFieldsList());
    } else {
      return new Permission();
    }
  }

  static Timestamp convertToProto(Instant model) {
    return Timestamp.newBuilder()
        .setSeconds(model.getEpochSecond())
        .setNanos(model.getNano())
        .build();
  }

  static SharedProto.Permission convertToProto(Permission model) {
    if (model.fields() == null) {
      return SharedProto.Permission.getDefaultInstance();
    }

    return SharedProto.Permission.newBuilder()
        .setValue(SharedProto.PermissionFields.newBuilder().addAllFields(model.fields()))
        .build();
  }
}
