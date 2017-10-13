package me.thanel.keepasst.base

import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.slackspace.openkeepass.domain.KeePassFile
import me.thanel.keepasst.KeePassStorage

abstract class BaseFragment : Fragment() {
    protected val database: KeePassFile?
        get() = KeePassStorage.get()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?): View? {
        return inflater.inflate(layoutResId, container, false)
    }

    abstract val layoutResId: Int
        @LayoutRes get
}
