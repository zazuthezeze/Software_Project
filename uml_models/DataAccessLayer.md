Overview of DataAccessLayer:
The DataAccessLayer is a system that is responsible for receiving all incoming data from the signal generator and converting it into usable objects that the rest of the system can work with.

The whole chain starts with the DataListener interface which defines the contract for all listeners, since it is an interface it has no attributes and only defines the methods that all subclasses must implement. 

The three subclasses: TCPDataListener, WebSocketDataListener and FileDataListener all implement this interface and each is specialized in receiving data from a specific source. 

TCPDataListener handles TCP connections, WebSocketDataListener handles WebSocket connections and FileDataListener handles file based input. The relationship between the subclasses and the DataListener is one of realization since they all implement the interface.

The DataListener has a dependency relationship with the DataParser since the data received by the DataListener is temporarily passed to the DataParser to be converted into a usable PatientData object, once the data is parsed it is no longer needed by the DataListener so it is a dependency and not an association.

The DataParser then has a dependency relationship with the DataSourceAdapter since the parsed data is temporarily passed to the DataSourceAdapter which will validate it and send it to DataStorage, once passed the DataParser no longer needs it so it is also a dependency.

The DataSourceAdapter has an association relationship with DataStorage since it permanently stores a reference to DataStorage as it will always need to know where to send the validated data, the multiplicity of this relationship is 1 to 1 as there is only one of each.


Design features:
The DataListener interface was introduced so that the rest of the system does not need to know how data arrives, meaning any of the three listeners could be swapped out or a new one added without changing anything else in the system.
Each class has a single responsibility, DataListener only receives, DataParser only parses, DataSourceAdapter only validates and forwards, this makes the system easy to maintain and extend.
The use of an interface means the system is open for extension since new listener types can be added in the future without modifying any existing code.
