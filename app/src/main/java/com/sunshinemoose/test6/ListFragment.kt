package com.sunshinemoose.test6

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sunshinemoose.test6.databinding.FragmentListBinding
import java.io.InputStream

/**
 * A simple fragment to show the main list, just to keep encapsulation
 */
class ListFragment : Fragment() {
    private var binding: FragmentListBinding? = null
    private var presenter: Contract.Presenter? = null

    private val adapter = MainRecyclerAdapter(emptyList())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = MainPresenter(object : Contract.ViewListener {
            override fun updateList(items: List<String>) {
                adapter.setItems(items)
            }

            override fun showDetailView(driver: String, shipment: String) {
                showDetailDialog(driver, shipment)
            }

            override fun getInputStream(): InputStream {
                return resources.assets.open("data.json")
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return FragmentListBinding.inflate(inflater, container, false).apply {
            recycler.adapter = adapter
            binding = this
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter?.let {
            adapter.setItemClickListener(it::onItemClicked)
        }
        presenter?.viewCreated()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter = null
    }

    fun showDetailDialog(driver: String, shipment: String) {
        DetailFragment.getFrag(driver, shipment).show(childFragmentManager, DetailFragment::class.qualifiedName)
    }
}
