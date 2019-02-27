import Foundation
import Capacitor
import CallKit

/**
 * Please read the Capacitor iOS Plugin Development Guide
 * here: https://capacitor.ionicframework.com/docs/plugins/ios
 */
@objc(CallObserver)
public class CallObserver: CAPPlugin {
    let callObserver = CallObserverSingleton.sharedInstance
    var callStatus = CallStatus.none
    
    public override init() {
        super.init()
        self.callObserver.setDelegate(self, queue: nil)
    }
    
//    @objc func echo(_ call: CAPPluginCall) {
//        let value = call.getString("value") ?? ""
//        call.success([
//            "value": value
//        ])
//    }
    
    @objc func observe(_ call: CAPPluginCall) {
        call.success([
            "status": self.callStatus
        ])
    }
}

extension CallObserver: CXCallObserverDelegate {
    public func callObserver(_ callObserver: CXCallObserver, callChanged call: CXCall) {
        print("CALL: \(call.description)")
        
        if !call.isOutgoing && !call.hasConnected && !call.hasEnded {
            //  Call is incoming
            print("CXCallObserverDelegate: Call is incoming.")
            self.callStatus = CallStatus.incoming
        }
        
        if call.isOutgoing && !call.hasConnected {
            //  Dialing out
            print("CXCallObserverDelegate: Call is dialing.")
            self.callStatus = CallStatus.dialing
            
            switch UIApplication.shared.applicationState {
            case .active:
                print("APP STATE: Active")
                break
            case .background:
                print("APP STATE: Background")
                break
            case .inactive:
                print("APP STATE: Inactive")
                break
            }
        }
        
        if call.hasConnected && !call.hasEnded {
            //  Call is ongoing
            print("CXCallObserverDelegate: Call is ongoing.")
            self.callStatus = CallStatus.ongoing
        }
        
        if call.hasEnded {
            //  Call ended
            print("CXCallObserverDelegate: Call has ended.")
            self.callStatus = CallStatus.ended
        }
    }
}

public class CallObserverSingleton {
    
    static let sharedInstance = CXCallObserver()
    private init() {} //This prevents others from using the default '()' initializer for this class.
    
}

enum CallStatus {
    case none
    case incoming
    case dialing
    case ongoing
    case ended
}
