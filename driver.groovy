metadata {
    definition(name: 'LIFX Scene Device by @andrewkirc', namespace: 'andrewkirc', author: 'Andrew Kirchofer') {
        capability 'Actuator'
        capability 'Switch'
        command 'activateScene'
    }

    preferences {
        input name: 'apiToken', type: 'string', title: 'LIFX API Token', required: true, displayDuringSetup: false
        input name: 'sceneId', type: 'string', title: 'LIFX Scene ID', required: true, displayDuringSetup: false
    }
}

def installed() {
    initialize()
}

def updated() {
    initialize()
}

def initialize() {
// Initialize device
}

def parse(String description) {
// Parse events (if any)
}

def on() {
    activateScene()
}

def off() {
    deactivateScene()
}

def activateScene() {
    log.debug "Activating LIFX scene with ID: ${sceneId}"
    def authString = "${apiToken}:"
    def authEncoded = authString.bytes.encodeBase64().toString()
    def headers = ['Authorization': "Basic ${authEncoded}"]

    def params = [
        uri: 'https://api.lifx.com',
        path: "/v1/scenes/scene_id:${sceneId}/activate",
        headers: headers,
        contentType: 'application/json'
    ]

    try {
        httpPut(params) { resp ->
            if (resp.status == 200) {
                log.debug 'Scene activated successfully'
                sendEvent(name: 'switch', value: 'on')
                state.lastSelector = getSceneLightsSelector()
            } else {
                log.error "Error activating scene: HTTP ${resp.status} - ${resp.data}"
            }
        }
    } catch (Exception e) {
        log.error "Exception in activateScene: ${e.message}"
    }
}

def deactivateScene() {
    if (!state.lastSelector) {
        log.warn 'No lights to turn off. Activate the scene first.'
        return
    }

    log.debug 'Turning off lights associated with the scene'
    def authString = "${apiToken}:"
    def authEncoded = authString.bytes.encodeBase64().toString()
    def headers = ['Authorization': "Basic ${authEncoded}"]
    def body = [power: 'off']

    def params = [
        uri: 'https://api.lifx.com',
        path: "/v1/lights/${state.lastSelector}/state",
        headers: headers,
        body: body,
        contentType: 'application/json'
    ]

    try {
        httpPut(params) { resp ->
            if (resp.status == 207 || resp.status == 200) {
                log.debug 'Lights turned off successfully'
                sendEvent(name: 'switch', value: 'off')
                state.remove('lastSelector')
            } else {
                log.error "Error turning off lights: HTTP ${resp.status} - ${resp.data}"
            }
        }
    } catch (Exception e) {
        log.error "Exception in deactivateScene: ${e.message}"
    }
}

def getSceneLightsSelector() {
    log.debug 'Fetching scene details to get light selectors'
    def authString = "${apiToken}:"
    def authEncoded = authString.bytes.encodeBase64().toString()
    def headers = ['Authorization': "Basic ${authEncoded}"]

    def params = [
        uri: 'https://api.lifx.com',
        path: "/v1/scenes/scene_id:${sceneId}",
        headers: headers,
        contentType: 'application/json'
    ]

    try {
        httpGet(params) { resp ->
            if (resp.status == 200) {
                def lights = resp.data.lights
                def selectors = lights.collect { it.id }
                def selectorString = 'id:' + selectors.join(',')
                log.debug "Scene lights selector: ${selectorString}"
                return selectorString
            } else {
                log.error "Error fetching scene details: HTTP ${resp.status} - ${resp.data}"
                return null
            }
        }
    } catch (Exception e) {
        log.error "Exception in getSceneLightsSelector: ${e.message}"
        return null
    }
}
