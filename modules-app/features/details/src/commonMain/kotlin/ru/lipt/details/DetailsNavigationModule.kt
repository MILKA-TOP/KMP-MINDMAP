package ru.lipt.details

import org.koin.dsl.module
import ru.lipt.details.editable.editableDetailsNavigationModule
import ru.lipt.details.uneditable.uneditableDetailsNavigationModule

val detailsNavigationModule = module {
    includes(
        editableDetailsNavigationModule,
        uneditableDetailsNavigationModule,
    )
}
