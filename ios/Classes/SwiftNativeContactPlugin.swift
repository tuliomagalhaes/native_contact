import Flutter
import UIKit
import Contacts
import ContactsUI

@available(iOS 9.0, *)
public class SwiftNativeContactPlugin: NSObject, FlutterPlugin, CNContactViewControllerDelegate {

    private let rootViewController: UIViewController;
    private var contactNavigationController: UINavigationController?

    init(fromViewController rootViewController: UIViewController) {
        self.rootViewController = rootViewController;
    }

    public static func register(with registrar: FlutterPluginRegistrar) {
        let channel = FlutterMethodChannel(name: "native_contact", binaryMessenger: registrar.messenger())
        if let rootViewController = UIApplication.shared.delegate?.window??.rootViewController {
            let plugin = SwiftNativeContactPlugin(fromViewController: rootViewController)
            registrar.addMethodCallDelegate(plugin, channel: channel)
        }
    }

    public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
        let contact = dictionaryToContact(dictionary: call.arguments as! [String : Any])
        addNewContact(contact: contact)
        result(nil)
    }

    private func addNewContact(contact: CNContact) {
        let contactViewController = CNContactViewController(forUnknownContact: contact)
        self.contactNavigationController = UINavigationController(rootViewController: contactViewController)
        
        contactViewController.contactStore = CNContactStore()
        contactViewController.allowsActions = true
        contactViewController.delegate = self
        contactViewController.allowsEditing = false
        contactViewController.navigationItem.leftBarButtonItem = UIBarButtonItem(barButtonSystemItem: UIBarButtonItem.SystemItem.done, target: self, action: #selector(cancelAddContactTapped))
        
        self.rootViewController.present(contactNavigationController!, animated: true)
    }
    
    @objc private func cancelAddContactTapped() {
        if let contactNavigationController = contactNavigationController {
            contactNavigationController.dismiss(animated: true, completion: nil)
        }
    }

    private func dictionaryToContact(dictionary : [String:Any]) -> CNMutableContact{
        let contact = CNMutableContact()

        contact.givenName = dictionary["name"] as? String ?? ""
        contact.organizationName = dictionary["company"] as? String ?? ""
        contact.jobTitle = dictionary["jobTitle"] as? String ?? ""
        if dictionary["website"] as? String != nil {
            contact.urlAddresses.append(CNLabeledValue(label:CNLabelURLAddressHomePage, value:NSString(string:(dictionary["website"] as? String ?? ""))))
        }
        
        if let avatarData = (dictionary["avatar"] as? FlutterStandardTypedData)?.data {
            contact.imageData = avatarData
        }

        if let phoneNumbers = dictionary["phones"] as? [[String:Any]]{
            for phone in phoneNumbers where phone["value"] != nil {
                contact.phoneNumbers.append(CNLabeledValue(label:getPhoneLabel(label:phone["label"] as? Int),value:CNPhoneNumber(stringValue:phone["value"]! as! String)))
            }
        }

        if let emails = dictionary["emails"] as? [[String:Any]]{
            for email in emails where nil != email["value"] {
                contact.emailAddresses.append(CNLabeledValue(label:"", value:email["value"]! as! NSString))
            }
        }

        if let postalAddresses = dictionary["postalAddresses"] as? [[String:Any]]{
            for postalAddress in postalAddresses{
                let newAddress = CNMutablePostalAddress()
                newAddress.street = postalAddress["street"] as? String ?? ""
                newAddress.city = postalAddress["city"] as? String ?? ""
                newAddress.postalCode = postalAddress["postcode"] as? String ?? ""
                newAddress.country = postalAddress["country"] as? String ?? ""
                newAddress.state = postalAddress["region"] as? String ?? ""
                let label = postalAddress["label"] as? String ?? ""
                contact.postalAddresses.append(CNLabeledValue(label:label, value:newAddress))
            }
        }

        return contact
    }

    private func contactToDictionary(contact: CNContact) -> [String:Any]{

        var result = [String:Any]()

        result["displayName"] = CNContactFormatter.string(from: contact, style: CNContactFormatterStyle.fullName)
        result["name"] = contact.givenName
        result["company"] = contact.organizationName
        result["jobTitle"] = contact.jobTitle
        if let avatarData = contact.thumbnailImageData {
            result["avatar"] = FlutterStandardTypedData(bytes: avatarData)
        }

        var phoneNumbers = [[String:String]]()
        for phone in contact.phoneNumbers{
            var phoneDictionary = [String:String]()
            phoneDictionary["value"] = phone.value.stringValue
            phoneDictionary["label"] = "other"
            if let label = phone.label{
                phoneDictionary["label"] = CNLabeledValue<NSString>.localizedString(forLabel: label)
            }
            phoneNumbers.append(phoneDictionary)
        }
        result["phones"] = phoneNumbers

        var emailAddresses = [[String:String]]()
        for email in contact.emailAddresses{
            var emailDictionary = [String:String]()
            emailDictionary["value"] = String(email.value)
            emailDictionary["label"] = "other"
            if let label = email.label{
                emailDictionary["label"] = CNLabeledValue<NSString>.localizedString(forLabel: label)
            }
            emailAddresses.append(emailDictionary)
        }
        result["emails"] = emailAddresses

        var postalAddresses = [[String:String]]()
        for address in contact.postalAddresses{
            var addressDictionary = [String:String]()
            addressDictionary["label"] = ""
            if let label = address.label{
                addressDictionary["label"] = CNLabeledValue<NSString>.localizedString(forLabel: label)
            }
            addressDictionary["street"] = address.value.street
            addressDictionary["city"] = address.value.city
            addressDictionary["postcode"] = address.value.postalCode
            addressDictionary["region"] = address.value.state
            addressDictionary["country"] = address.value.country

            postalAddresses.append(addressDictionary)
        }
        result["postalAddresses"] = postalAddresses

        return result
    }

    private func getPhoneLabel(label: Int?) -> String{
        let labelValue = label ?? 1
        switch(labelValue){
        case 2: return CNLabelPhoneNumberMobile
        case 3: return CNLabelPhoneNumberiPhone
        default: return CNLabelPhoneNumberMain
        }
    }
}
