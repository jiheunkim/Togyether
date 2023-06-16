    package com.example.togyether

    import android.content.Context
    import android.os.Bundle
    import android.text.TextUtils.replace
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import androidx.fragment.app.Fragment
    import com.example.togyether.databinding.FragmentDutchpayFrameBinding
    import com.example.togyether.databinding.FragmentDutchpayGroupFrameBinding


    class DutchpayGroupFrameFragment(var memberList: ArrayList<memberData>) : Fragment() {
        lateinit var binding: FragmentDutchpayGroupFrameBinding
        lateinit var dActivity: MainActivity

        var dutchpayGroupFragment = DutchpayGroupFragment(memberList)

        override fun onAttach(context: Context) {
            super.onAttach(context)
            dActivity = context as MainActivity
        }

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            binding = FragmentDutchpayGroupFrameBinding.inflate(layoutInflater, container, false)
            val view = binding.root
            with(view) {
                childFragmentManager.beginTransaction().apply {
                    replace(R.id.root_fragment, dutchpayGroupFragment)
                    commit()
                }
            }
            return view
        }

    }
