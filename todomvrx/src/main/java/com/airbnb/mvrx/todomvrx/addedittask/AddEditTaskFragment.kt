/*
 * Copyright 2017, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.airbnb.mvrx.todomvrx.addedittask

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.navigation.fragment.findNavController
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.args
import com.airbnb.mvrx.todomvrx.TasksViewModel
import com.airbnb.mvrx.todomvrx.core.BaseFragment
import com.airbnb.mvrx.todomvrx.data.Task
import com.airbnb.mvrx.todomvrx.data.findTask
import com.airbnb.mvrx.todomvrx.todoapp.R
import com.airbnb.mvrx.withState
import kotlinx.android.parcel.Parcelize


@Parcelize
data class AddEditTaskArgs(val id: String? = null) : Parcelable

/**
 * Main UI for the add task screen. Users can enter a task title and description.
 */
class AddEditTaskFragment : BaseFragment() {

    private val viewModel by activityViewModel(TasksViewModel::class)
    private val args: AddEditTaskArgs by args()

    private lateinit var titleView: EditText
    private lateinit var descriptionView: EditText

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.frag_add_edit, container, false).apply {
            fab = findViewById(R.id.fab)
            titleView = findViewById(R.id.task_title)
            descriptionView = findViewById(R.id.task_description)
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        args.id?.let { id ->
            withState(viewModel) { state ->
                val task = state.tasks.findTask(id) ?: throw IllegalStateException("Unable to find task $id")
                titleView.setText(task.title)
                descriptionView.setText(task.description)
            }
        }
        fab.setImageResource(if (args.id == null) R.drawable.ic_add else R.drawable.ic_edit)
        fab.setOnClickListener {
            withState(viewModel) { state ->
                val task = state.tasks.findTask(args.id) ?: Task()
                        .copy(
                                title = titleView.text.toString(),
                                description = descriptionView.text.toString()
                        )
                viewModel.saveTask(task)
                findNavController().navigateUp()
            }
        }
    }

    override fun invalidate() {
        // Do nothing.
    }
}
