package com.example.tokenlabchallenge.movie

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.example.tokenlabchallenge.databinding.FragmentMovieBinding

class MovieFragment : Fragment() {

    private val viewModel: MovieViewModel by lazy {
        ViewModelProviders.of(this).get(MovieViewModel::class.java)
    }

    // Inflates the layout with Data Binding, sets its lifecycle owner to the OverviewFragment
    // to enable Data Binding to observe LiveData, and sets up the RecyclerView with an adapter.
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding = FragmentMovieBinding.inflate(inflater)

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.setLifecycleOwner(this)

        // Giving the binding access to the OverviewViewModel
        binding.viewModel = viewModel

        // Sets the adapter of the photosGrid RecyclerView
        binding.photosGrid.adapter = ImageGridAdapter(ImageGridAdapter.OnClickListener {
            viewModel.displayPropertyDetails(it)
        })

        viewModel.navigateToSelectedProperty.observe(viewLifecycleOwner, Observer {
            if ( null != it ) {
                this.findNavController().navigate(MovieFragmentDirections.actionShowDetail(it))
                viewModel.displayPropertyDetailsComplete()
            }
        })

        return binding.root
    }
}
