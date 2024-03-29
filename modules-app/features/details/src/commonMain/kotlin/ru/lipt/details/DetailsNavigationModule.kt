package ru.lipt.details

import org.koin.dsl.module
import ru.lipt.core.kover.IgnoreKover
import ru.lipt.details.editable.editableDetailsNavigationModule
import ru.lipt.details.uneditable.uneditableDetailsNavigationModule

@IgnoreKover
val detailsNavigationModule = module {
    includes(
        editableDetailsNavigationModule,
        uneditableDetailsNavigationModule,
    )
}
