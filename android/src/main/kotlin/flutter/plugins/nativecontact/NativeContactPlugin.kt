package flutter.plugins.nativecontact

import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.PluginRegistry
import io.flutter.plugin.common.PluginRegistry.Registrar
import io.flutter.plugin.common.PluginRegistry.ActivityResultListener
import android.content.Intent
import android.provider.ContactsContract
import android.provider.ContactsContract.Contacts
import android.provider.ContactsContract.Intents
import mapToContact
import NativeContact
import android.content.ContentValues
import java.util.ArrayList
import android.R.attr.data
import android.provider.ContactsContract.CommonDataKinds.Email
import android.provider.ContactsContract.CommonDataKinds.Phone
import android.provider.ContactsContract.CommonDataKinds.Website

class NativeContactPlugin constructor(private val registrar: Registrar): MethodCallHandler, PluginRegistry.ActivityResultListener {

    companion object {
        @JvmStatic
        fun registerWith(registrar: Registrar): Unit {
            val channel = MethodChannel(registrar.messenger(), "native_contact")
            channel.setMethodCallHandler(NativeContactPlugin(registrar))
        }
    }

    override fun onMethodCall(call: MethodCall, result: Result): Unit {
        if (call.method.equals("addNewContact")) {
            addNewContact(mapToContact(call.arguments as Map<String, Any>))
            result.success(null)
        } else {
            result.notImplemented()
        }
    }

    private fun addNewContact(contact: NativeContact) {
        val intent = Intent(Intent.ACTION_INSERT).apply {
            type = ContactsContract.Contacts.CONTENT_TYPE
            putExtra(ContactsContract.Intents.Insert.NAME, contact.name)
            putExtra(ContactsContract.Intents.Insert.COMPANY, contact.company)
            putExtra(ContactsContract.Intents.Insert.JOB_TITLE, contact.jobTitle)
     putExtra(ContactsContract.Intents.Insert.JOB_TITLE, contact.jobTitle)
        }

        val data = ArrayList<ContentValues>()

        for (email in contact.emails) {
            val emailContentValues = ContentValues()
            emailContentValues.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
            emailContentValues.put(ContactsContract.CommonDataKinds.Email.ADDRESS, email.value)
            emailContentValues.put(ContactsContract.CommonDataKinds.Email.LABEL, email.label)
            emailContentValues.put(ContactsContract.CommonDataKinds.Email.TYPE, Email.TYPE)
            data.add(emailContentValues)
        }

        for (phone in contact.phones) {
            val phoneContentValues = ContentValues()
            phoneContentValues.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
            phoneContentValues.put(ContactsContract.CommonDataKinds.Phone.NUMBER, phone.value)
            phoneContentValues.put(ContactsContract.CommonDataKinds.Phone.LABEL, phone.label)
            phoneContentValues.put(ContactsContract.CommonDataKinds.Phone.TYPE, Phone.TYPE)
            data.add(phoneContentValues)
        }

        val websiteContentValues = ContentValues()
        websiteContentValues.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE);
        websiteContentValues.put(ContactsContract.CommonDataKinds.Website.URL, contact.website);
        websiteContentValues.put(ContactsContract.CommonDataKinds.Website.TYPE, Website.TYPE_HOME);
        data.add(websiteContentValues)

        intent.putExtra(ContactsContract.Intents.Insert.DATA, data)

        startIntent(intent)
    }

    private fun startIntent(intent: Intent) {
        val context = if (registrar.activity() != null) {
            registrar.activity()
        } else {
            registrar.context()
        }

        context.startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent): Boolean {
        return false
    }
}
