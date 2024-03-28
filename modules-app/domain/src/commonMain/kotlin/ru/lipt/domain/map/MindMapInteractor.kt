package ru.lipt.domain.map

import ru.lipt.core.cache.CachePolicy
import ru.lipt.core.uuid.randomUUID
import ru.lipt.domain.catalog.CatalogRepository
import ru.lipt.domain.map.models.MapRemoveType
import ru.lipt.domain.map.models.NodesEditResponseRemote
import ru.lipt.domain.map.models.QuestionsEditResponseRemote
import ru.lipt.domain.map.models.SummaryEditMapResponseRemote
import ru.lipt.domain.map.models.SummaryViewMapResponseRemote
import ru.lipt.domain.map.models.TestResultViewResponseRemote
import ru.lipt.domain.map.models.TestingCompleteRequestRemote
import ru.lipt.domain.map.models.TestsEditResponseRemote
import ru.lipt.domain.map.models.abstract.SummaryMapResponseRemote
import ru.lipt.domain.map.models.update.AnswerUpdateParam
import ru.lipt.domain.map.models.update.MapsUpdateRequestParams
import ru.lipt.domain.map.models.update.NodesUpdateParam
import ru.lipt.domain.map.models.update.QuestionUpdateParam
import ru.lipt.domain.map.models.update.TestUpdateParam
import ru.lipt.domain.map.models.update.UpdatedListComponent

class MindMapInteractor(
    private val mapRepository: MindMapRepository,
    private val mapViewRepository: MindViewMapRepository,
    private val catalogRepository: CatalogRepository,
) {
    suspend fun getMap(id: String, cachePolicy: CachePolicy = CachePolicy.ALWAYS) = mapRepository.fetch(id, cachePolicy)!!

    suspend fun fetchViewMap(mapId: String, userId: String, cachePolicy: CachePolicy = CachePolicy.ALWAYS): SummaryViewMapResponseRemote =
        mapViewRepository.fetch(Pair(mapId, userId), cachePolicy)!!

    // 1
    suspend fun addNewNodeToMap(mapId: String, parentId: String, title: String): SummaryMapResponseRemote {
        val newNodeId = randomUUID()
        return mapRepository.updateCache(mapId) {
            (this as? SummaryEditMapResponseRemote)?.let {
                val catalogSection = it.nodes.filter { it.parentNodeId == parentId }
                it.copy(
                    nodes = nodes + NodesEditResponseRemote(
                        id = newNodeId, label = title, priorityPosition = catalogSection.size + 1, parentNodeId = parentId
                    )
                )
            } ?: this
        }.let {
            mapRepository.updateRequestCache(mapId) { map ->
                val updatedNodes = this.nodes
                val list = nodes.insert
                val catalogSection = map.nodes.filter { it.parentNodeId == parentId }
                val priorityPosition = catalogSection.size + 1
                copy(
                    nodes = updatedNodes.copy(
                        insert = list + NodesUpdateParam(
                            nodeId = newNodeId,
                            mapId = mapId,
                            title = title,
                            priorityNumber = priorityPosition,
                            parentNodeId = parentId
                        )
                    )
                )
            }
        }.let { getMap(mapId) }
    }

    suspend fun getEditableNode(mapId: String, nodeId: String) =
        (getMap(mapId) as SummaryEditMapResponseRemote).nodes.first { it.id == nodeId }

    suspend fun getViewNode(mapId: String, nodeId: String) = (getMap(mapId) as SummaryViewMapResponseRemote).nodes.first { it.id == nodeId }

    // 1
    suspend fun deleteMap(mapId: String) = mapRepository.deleteMap(mapId).also {
        catalogRepository.removeMap(mapId)
    }

    suspend fun eraseMap(mapId: String, type: MapRemoveType) = mapRepository.eraseMap(mapId, type).also {
        catalogRepository.removeMap(mapId)
    }

    suspend fun updateMindMap(mapId: String): SummaryMapResponseRemote {
        mapRepository.updateMindMap(mapId)
        return mapRepository.fetch(mapId, CachePolicy.REFRESH)!!
    }

    // 1
    suspend fun updateNodePosition(mapId: String, nodeId: String, index: Int) = mapRepository.updateCache(mapId) {
        (this as? SummaryEditMapResponseRemote)?.let { map ->
            val parentNodeId = map.nodes.first { it.id == nodeId }.parentNodeId
            val catalogSection = map.nodes.filter { it.parentNodeId == parentNodeId }.sortedBy { it.priorityPosition }.map {
                when {
                    it.id == nodeId -> it.copy(priorityPosition = index)
                    it.priorityPosition < index -> it
                    else -> it.copy(priorityPosition = it.priorityPosition + 1)
                }
            }.associateBy { it.id }

            map.copy(nodes = nodes.map {
                if (it.id in catalogSection) catalogSection[it.id]!! else it
            })
        } ?: this
    }.let {
        mapRepository.updateRequestCache(mapId) { map ->
            var updatedParams = this
            val parentNodeId = map.nodes.first { it.id == nodeId }.parentNodeId
            val nodes = map.nodes.filter { it.parentNodeId == parentNodeId }
            nodes.map { node ->
                updatedParams = updatedParams.updateNodeCache(node) {
                    copy(
                        insert = insert + NodesUpdateParam(
                            nodeId = node.id,
                            mapId = mapId,
                            parentNodeId = node.parentNodeId,
                            title = node.label,
                            details = node.description,
                            priorityNumber = node.priorityPosition
                        )
                    )
                }
            }
            updatedParams
        }
    }.let { getMap(mapId) }

    // 1
    suspend fun saveTitleAndData(mapId: String, title: String, description: String) = mapRepository.updateCache(
        mapId
    ) {
        (this as? SummaryEditMapResponseRemote)?.copy(title = title, description = description) ?: this
    }.also {
        mapRepository.updateRequestCache(mapId) {
            copy(title = title, description = description)
        }
    }

    suspend fun toggleNode(mapId: String, nodeId: String) = mapRepository.toggleNode(mapId, nodeId)

    // 1
    suspend fun saveNodeData(
        mapId: String, nodeId: String, title: String, description: String
    ) = mapRepository.updateCache(mapId) {
        (this as? SummaryEditMapResponseRemote)?.let { map ->
            copy(nodes = nodes.map { node ->
                if (node.id == nodeId) node.copy(
                    label = title,
                    description = description,
                ) else node
            })
        } ?: this
    }.let {
        mapRepository.updateRequestCache(mapId) { map ->
            val node = map.nodes.first { it.id == nodeId }
            updateNodeCache(node) {
                copy(
                    updated = nodes.updated + NodesUpdateParam(
                        nodeId = node.id,
                        mapId = mapId,
                        parentNodeId = node.parentNodeId,
                        title = node.label,
                        details = node.description,
                        priorityNumber = node.priorityPosition
                    )
                )
            }
        }
    }

    // 1
    suspend fun removeNode(mapId: String, nodeId: String) = mapRepository.updateCache(mapId) {
        (this as? SummaryEditMapResponseRemote)?.let { map ->
            val currentNode = map.nodes.first { it.id == nodeId }
            val parentNodeId = currentNode.parentNodeId
            copy(nodes = nodes.filter { it.id != nodeId }.map { node ->
                if (node.parentNodeId == nodeId) node.copy(parentNodeId = parentNodeId) else node
            })
        } ?: this
    }.let {
        mapRepository.updateRequestCache(mapId) { map ->
            val updatedNodes = this.nodes
            copy(nodes = when {
                nodes.insert.any { it.nodeId == nodeId } -> updatedNodes.copy(insert = nodes.insert.filter { it.nodeId != nodeId })
                nodes.updated.any { it.nodeId == nodeId } -> updatedNodes.copy(
                    insert = nodes.updated.filter { it.nodeId != nodeId },
                    removed = nodes.removed + nodeId,
                )
                else -> updatedNodes.copy(removed = nodes.removed + nodeId)
            }
            )
        }
    }

    // questions
    @Suppress("LongMethod")
    suspend fun updateQuestions(mapId: String, nodeId: String, testId: String, questions: List<QuestionsEditResponseRemote>) =
        mapRepository.updateRequestCache(mapId) { map ->
            val node = map.nodes.first { it.id == nodeId }
            var updatedMapParams = this
            if (node.test == null) {
                val updatedTests = updatedMapParams.tests
                updatedMapParams = updatedMapParams.copy(
                    tests = updatedTests.copy(insert = updatedTests.insert + TestUpdateParam(testId, nodeId))
                )
            }
            val cachedQuestions = node.test?.questions.orEmpty()
            val cachedAnswers = node.test?.questions?.flatMap { it.answers }.orEmpty()

            val inputQuestions = questions.map { question ->
                QuestionUpdateParam(
                    questionId = question.id,
                    testId = question.testId,
                    questionType = question.questionType,
                    title = question.questionText
                )
            }
            val insertedQuestions = inputQuestions.filter { inputQuestion ->
                cachedQuestions.none { it.id == inputQuestion.questionId }
            }
            val updatedQuestions = inputQuestions.filter { inputQuestion ->
                cachedQuestions.any { it.id == inputQuestion.questionId }
            }

            // Mapping updated answers to AnswerUpdateParam
            val inputAnswers = questions.flatMap { question ->
                question.answers.map { answer ->
                    AnswerUpdateParam(
                        questionId = answer.questionId,
                        answerId = answer.id,
                        title = answer.answerText,
                        isCorrect = answer.isCorrect
                    )
                }
            }

            val insertedAnswers = inputAnswers.filter { inputAnswer ->
                cachedAnswers.none { it.id == inputAnswer.answerId }
            }
            val updatedAnswers = inputAnswers.filter { inputAnswer ->
                cachedAnswers.any { it.id == inputAnswer.answerId }
            }

            // Identify old questions that are not in the new list for removal
            val oldQuestionsForRemoval = this.questions.insert.filter { oldQuestion ->
                inputQuestions.none { it.questionId == oldQuestion.questionId }
            }.map { it.questionId } + this.questions.updated.filter { oldQuestion ->
                inputQuestions.none { it.questionId == oldQuestion.questionId }
            }.map { it.questionId }

            // Identify old answers that are not in the new list for removal
            val oldAnswersForRemoval = this.answers.insert.filter { oldAnswer ->
                inputAnswers.none { it.answerId == oldAnswer.answerId }
            }.map { it.answerId } + this.answers.updated.filter { oldAnswer ->
                inputAnswers.none { it.answerId == oldAnswer.answerId }
            }.map { it.answerId }

            // Processing questions: insert, update, and remove
            val updatedInsertQuestions = this.questions.insert.map { cachedQuestionRequest ->
                val updatedInsertQuestion = insertedQuestions.firstOrNull { it.questionId == cachedQuestionRequest.questionId }
                updatedInsertQuestion ?: cachedQuestionRequest
            }

            val updatedUpdateQuestions = this.questions.updated.map { cachedQuestionRequest ->
                val updatedUpdateQuestion = updatedQuestions.firstOrNull { it.questionId == cachedQuestionRequest.questionId }
                updatedUpdateQuestion ?: cachedQuestionRequest
            }

            val newInsertQuestions = insertedQuestions.filter { updatedQuestion ->
                updatedInsertQuestions.none { it.questionId == updatedQuestion.questionId } &&
                        updatedUpdateQuestions.none { it.questionId == updatedQuestion.questionId }
            }

            val newUpdatedQuestions = updatedQuestions.filter { updatedQuestion ->
                updatedInsertQuestions.none { it.questionId == updatedQuestion.questionId } &&
                        updatedUpdateQuestions.none { it.questionId == updatedQuestion.questionId }
            }

            // Processing answers: insert, update, and remove
            val updatedInsertAnswers = this.answers.insert.map { cachedAnswerRequest ->
                val updatedInsertAnswer = insertedAnswers.firstOrNull { it.answerId == cachedAnswerRequest.answerId }
                updatedInsertAnswer ?: cachedAnswerRequest
            }

            val updatedUpdateAnswers = this.answers.updated.map { cachedAnswerRequest ->
                val updatedUpdateAnswer = updatedAnswers.firstOrNull { it.answerId == cachedAnswerRequest.answerId }
                updatedUpdateAnswer ?: cachedAnswerRequest
            }

            val newInsertAnswers = insertedAnswers.filter { updatedAnswer ->
                updatedInsertAnswers.none { it.answerId == updatedAnswer.answerId } &&
                        updatedUpdateAnswers.none { it.answerId == updatedAnswer.answerId }
            }

            val newUpdatedAnswers = updatedAnswers.filter { updatedAnswer ->
                updatedInsertAnswers.none { it.answerId == updatedAnswer.answerId } &&
                        updatedUpdateAnswers.none { it.answerId == updatedAnswer.answerId }
            }

            // Create a new instance of MapsUpdateRequestParams with updated lists
            updatedMapParams.copy(
                questions = UpdatedListComponent(
                    insert = updatedInsertQuestions + newInsertQuestions,
                    updated = updatedUpdateQuestions + newUpdatedQuestions,
                    removed = (this.questions.removed + oldQuestionsForRemoval).distinct()
                ),
                answers = UpdatedListComponent(
                    insert = updatedInsertAnswers + newInsertAnswers,
                    updated = updatedUpdateAnswers + newUpdatedAnswers,
                    removed = (this.answers.removed + oldAnswersForRemoval).distinct()
                )
            )
        }.also {
            mapRepository.updateCache(mapId) {
                (this as? SummaryEditMapResponseRemote)?.let { map ->
                    val currentNode = map.nodes.first { it.id == nodeId }
                    val updatedNode = currentNode.copy(test = TestsEditResponseRemote(id = testId, nodeId = nodeId, questions = questions))
                    map.copy(nodes = map.nodes.map { node ->
                        if (node.id == nodeId) updatedNode else node
                    })
                } ?: this
            }
        }

    private fun MapsUpdateRequestParams.updateNodeCache(
        node: NodesEditResponseRemote,
        elseUpdate: UpdatedListComponent<NodesUpdateParam, String>.() -> UpdatedListComponent<NodesUpdateParam, String>
    ): MapsUpdateRequestParams {
        val nodeId = node.id
        val updatedNodes = this.nodes
        return copy(nodes = when {
            nodes.insert.any { it.nodeId == nodeId } -> updatedNodes.copy(insert = updatedNodes.insert.map {
                if (it.nodeId == nodeId) it.copy(
                    title = node.label, details = node.description, priorityNumber = node.priorityPosition
                ) else it
            })
            nodes.updated.any { it.nodeId == nodeId } -> updatedNodes.copy(updated = nodes.updated.map {
                if (it.nodeId == nodeId) it.copy(
                    title = node.label, details = node.description, priorityNumber = node.priorityPosition
                ) else it
            })
            else -> updatedNodes.elseUpdate()
        }
        )
    }

    suspend fun sendTestAnswersForNode(
        mapId: String,
        nodeId: String,
        testId: String,
        testAnswers: TestingCompleteRequestRemote
    ): TestResultViewResponseRemote {
        val result = mapRepository.sendTestAnswersForNode(testId, testAnswers)
        mapRepository.updateCache(mapId) {
            (this as? SummaryViewMapResponseRemote)?.let { map ->
                val nodesUpdate = map.nodes.map { if (it.id == nodeId) it.copy(test = it.test?.copy(testResult = result)) else it }
                map.copy(nodes = nodesUpdate)
            } ?: this
        }
        return result
    }
}
