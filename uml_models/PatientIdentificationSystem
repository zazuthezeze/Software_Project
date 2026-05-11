Overview of PatientIdentificationSystem:

The PatientIdentificationSystem is a system for making sure that all incoming data from the signal generator is correctly linked to the right patient. 

The whole chain starts with the HospitalPatient class which contains all the patient information, this information will be retrieved by the PatientDataRetriever and passed onto PatientIdentifier so that it can Identify the correct Patient.

The HospitalPatient class has an aggregate relationship with a PatientDataRetriever since both can exist by themselves however, the diamond is on the PatientDataRetriever side since it needs the data whilst the HospitalPatient class does not need anything. This connection has a multiplicity of 0..* to 1 since there is only one retriever but many patients so we will get many different HospitalPatient objects.

The PatientDataRetriever has an associated relationship with the PatientIdentifier because the PatientIdentifier will constantly need to store the PatientDataRetriever's output to be able to identify the patient, the multiplicity of this relationship is 1 to 1 as there is only 1 of each.

In case of problems with patient identification the output will be passed to the IdentityManager which will handle errors and edge cases.

The relationship between the PatientIdentifier and the IdentityManager is one of dependence this is because the IdentityManager is only needed in case of errors or edge cases which should not occur regularly and their multiplicity is 1 to 1 as only one of each exist.

The relationship between the IdentityManager and the PatientDataRetriever is one of association as every time there is an error the IdentityManager will need all available information to attempt to resolve the error, so it will need to store the PatientDataRetriever output.

Design features:

The PatientDataRetriever was added as a middleman so that neither PatientIdentifier nor IdentityManager directly depend on HospitalPatient, meaning the HospitalPatient class could be swapped out for any other patient database without changing anything else in the system.

Each class has a single responsibility, PatientIdentifier only matches, PatientDataRetriever only fetches and IdentityManager only handles errors, this makes the system easier to maintain and extend.

The 1 to 0..* multiplicity between PatientDataRetriever and HospitalPatient means the system can handle any number of patients without any structural changes.

 
