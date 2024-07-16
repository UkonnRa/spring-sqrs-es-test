package com.ukonnra.springcqrsestest.shared.user;

import com.ukonnra.springcqrsestest.shared.Query;
import java.util.Set;

public record UserQuery(Set<String> loginName, String fullText) implements Query {}
