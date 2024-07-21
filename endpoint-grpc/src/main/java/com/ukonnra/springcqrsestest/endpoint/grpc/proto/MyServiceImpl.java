package com.ukonnra.springcqrsestest.endpoint.grpc.proto;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class MyServiceImpl extends MyServiceGrpc.MyServiceImplBase {

  @Override
  public void sayHello(SayHelloRequest request, StreamObserver<SayHelloResponse> responseObserver) {
    final var resp =
        SayHelloResponse.newBuilder().setMessage("Hello, " + request.getName()).build();
    responseObserver.onNext(resp);
    responseObserver.onCompleted();
  }
}
