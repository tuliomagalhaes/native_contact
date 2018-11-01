import 'package:flutter/material.dart';

import 'package:native_contact/native_contact.dart';

void main() => runApp(MyApp());

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: OutlineButton(
            child: Text("salvar"),
            onPressed: () {
              print("pressed");
              ContactInfo contact = ContactInfo();
              contact.name = "Túlio";
              contact.company = "Any Company";
              contact.jobTitle = "Developer";
              contact.website = "www.test.com";
              PostalAddress postalAddress = PostalAddress();
              postalAddress.city = "Any City";
              postalAddress.postcode = "38282-123";
              postalAddress.region = "Any region";
              postalAddress.street = "Any address, 797";
              postalAddress.country = "Any Country";
              contact.postalAddresses = [postalAddress];
              contact.emails = [Item(1, "test@test.com")];
              contact.phones = [Item(3, "551199999999")];

              NativeContact.addNewContact(contact);
            },
          ),
        ),
      ),
    );
  }
}
