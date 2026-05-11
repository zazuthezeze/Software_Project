Overview of AlerGenerationSystem:

AlertGenerationSystem is a system that generates alerts based on thresholds and through these alerts informs other systems of the need to take action.

The whole chain starts off with the AlertGenerator pulling information from DataStorage, AlertGenerator will constantly need this data so it should be an attribute of AlertGenerator and since it is an attribute and dependent on DataStorage it will have a directional association with DataStorage.

The AlertGenerator then checks whether there are any thresholds that are surpassed via the AlertThreshold class which can be customised on a per patient basis or on a patient type basis, this is done because not all patients are the same and some have different thresholds and by having an editable class for each patient we can easily account for this. The connection between the AlertThreshold and the AlertGenerator is an association with there being 1 AlertGenerator and many AlertThresholds, this is because: the AlertGenerator needs to check the data it received from the DataStorage and it does this through comparing it with the AlertThreshold requirements. If any of the thresholds are exceeded an Alert will be triggered.

The Alert is essentially a breakdown of what happened and to which patient and the connection between the Alert and the AlertGenerator is a dependency since the AlertGenerator requires information about the Alert class to be able to generate an alert object. 

This Alert is then taken and given to the AlertManager which will notify other systems of the need to take action. The connection between the Alert and the AlertManager is one of aggregation since the AlertManager and the Alert both can exist by themselves and the AlertManager only holds the alerts temporarily with there being many alerts and only one AlertManager.



