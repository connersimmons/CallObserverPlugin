# CallObserverPlugin

The purpose of this plugin is to start observing a phone call's status once it has been initiated. Then return the status to the app so we know when the phone call has completed.

### Phone Statuses
* none
* incoming
* dialing
* ongoing
* ended

### Example output
```
{
  "data": {
    "status": "ongoing"
  }
}
```
