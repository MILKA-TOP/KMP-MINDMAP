package ru.lipt.core.di

import org.koin.core.qualifier.StringQualifier
import org.koin.core.scope.Scope

val USER_SESSION_SCOPE_QUALIFIER = StringQualifier("USER_SESSION_SCOPE_QUALIFIER")
const val USER_SESSION_SCOPE_ID = "USER_SESSION_SCOPE_ID"

inline fun Scope.getUserSessionScope(): Scope = getKoin().getScope(USER_SESSION_SCOPE_ID)
