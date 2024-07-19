package com.ukonnra.springcqrsestest.database.jpa;

import com.ukonnra.springcqrsestest.database.jpa.journal.JournalPO_;
import com.ukonnra.springcqrsestest.database.jpa.journal.JournalUserPO_;
import com.ukonnra.springcqrsestest.database.jpa.user.UserPO_;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Set;
import liquibase.database.LiquibaseTableNamesFactory;
import liquibase.report.ShowSummaryGeneratorFactory;
import liquibase.ui.LoggerUIService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aot.hint.ExecutableMode;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.util.ReflectionUtils;

@Slf4j
public class DataJpaRuntimeHints implements RuntimeHintsRegistrar {
  @Override
  public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
    for (final var clz :
        List.of(
            LoggerUIService.class,
            LiquibaseTableNamesFactory.class,
            ShowSummaryGeneratorFactory.class)) {
      for (final var constructor : clz.getDeclaredConstructors()) {
        hints.reflection().registerConstructor(constructor, ExecutableMode.INVOKE);
      }
    }

    final var classes =
        Set.of(
            EventPO_.class,
            AbstractEntityPO_.class,
            UserPO_.class,
            JournalPO_.class,
            JournalUserPO_.class);

    for (final var clz : classes) {
      ReflectionUtils.doWithLocalFields(
          clz,
          (field) -> {
            final var modifiers = field.getModifiers();
            if (Modifier.isPublic(modifiers)
                && Modifier.isStatic(modifiers)
                && Modifier.isVolatile(modifiers)) {
              hints.reflection().registerField(field);
            }
          });
    }
  }
}
