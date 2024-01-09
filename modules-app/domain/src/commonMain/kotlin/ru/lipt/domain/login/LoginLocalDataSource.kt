package ru.lipt.domain.login

import ru.lipt.core.cache.InMemoryLocalDataSource

class LoginLocalDataSource : InMemoryLocalDataSource<String, Unit>()
