package com.moataz.photoweather.views.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.moataz.photoweather.databinding.FragmentPhotoListBinding
import com.moataz.photoweather.viewModels.ListViewModel
import com.moataz.photoweather.views.FullImageActivity
import com.moataz.photoweather.views.PhotosAdapter

class PhotoListFragment : Fragment() {
    private var _binding: FragmentPhotoListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ListViewModel by lazy { ViewModelProvider(this)[ListViewModel::class.java] }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPhotoListBinding.inflate(inflater, container, false)
        val view = binding.root
        viewModel.getPhotoListFromFolder()
        viewModel.photoList.observe(requireActivity()){ list ->
            binding.photoListRecyclerView.layoutManager = GridLayoutManager(context, 2)
            binding.photoListRecyclerView.adapter = PhotosAdapter(list) { file ->
                val intent = Intent(requireContext(), FullImageActivity::class.java)
                intent.putExtra("PhotoFile",file.absolutePath)
                startActivity(intent)
            }
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}