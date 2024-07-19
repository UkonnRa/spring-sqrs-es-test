package com.ukonnra.springcqrsestest.shared;

import com.ukonnra.springcqrsestest.shared.journal.JournalCommand;
import com.ukonnra.springcqrsestest.shared.journal.JournalEvent;
import com.ukonnra.springcqrsestest.shared.user.UserCommand;
import com.ukonnra.springcqrsestest.shared.user.UserEvent;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;

@Slf4j
public class SharedRuntimeHints implements RuntimeHintsRegistrar {
  @Override
  public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
    Set.of(UserCommand.class, UserEvent.class, JournalCommand.class, JournalEvent.class).stream()
        .flatMap(clz -> Stream.concat(Stream.of(clz), Arrays.stream(clz.getPermittedSubclasses())))
        .map(clz -> (Class<? extends Serializable>) clz)
        .forEach(
            clz -> {
              hints.serialization().registerType(clz);
              hints.reflection().registerType(clz);
            });
  }
}
