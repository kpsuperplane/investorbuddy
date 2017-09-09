package com.hackthenorth.pennapps.investorbuddy

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [ItemFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [ItemFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

data class ItemData(val portfolio: Boolean, val name: String, val cost: Float, val mcr: Float, val descriptor: String, val descriptorColor: Int = R.color.colorPrimary,  val shareCost: Float? = null);

class ItemFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_item, container, false)
    }

    fun init(itemData: ItemData){
        val view = getView()
        view!!.findViewById<TextView>(R.id.itemName).text = itemData.name

        val itemDescriptor = view.findViewById<TextView>(R.id.itemDescriptor)
        itemDescriptor.setTextColor(itemData.descriptorColor)
        itemDescriptor.text = itemData.descriptor

        view!!.findViewById<TextView>(R.id.itemCost).text = itemData.cost.toString()

        if (itemData.portfolio) {
            val itemShareCost = view!!.findViewById<TextView>(R.id.itemShareCost)
            itemShareCost.text = itemData.shareCost.toString() + "/share"
            itemShareCost.visibility = View.VISIBLE
        }

        view.findViewById<TextView>(R.id.itemMcr).text = (if (itemData.mcr < 0) "?" else itemData.mcr.toString()) + " mcr"

    }

    companion object {
        fun newInstance(): ItemFragment {
            val fragment = ItemFragment()
            return fragment
        }
    }
}// Required empty public constructor
