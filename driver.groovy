metadata {
    definition(name: 'LIFX Scene Device by @andrewkirc', namespace: 'andrewkirc', author: 'Andrew Kirchofer') {
        capability 'Actuator'
        capability 'Switch'
        command 'activateScene'
        command 'activateMorphEffect'
        command 'effectsOff'
    }

    preferences {
        input name: 'apiToken', type: 'string', title: 'LIFX API Token', required: true, displayDuringSetup: false
        input name: 'sceneId', type: 'string', title: 'LIFX Scene ID', required: true, displayDuringSetup: false
        input name: 'selector', type: 'string', title: 'LIFX Selector - https://api.developer.lifx.com/reference/selectors', required: true, displayDuringSetup: false
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
                log.debug "Using selector: ${selector}"
            } else {
                log.error "Error activating scene: HTTP ${resp.status} - ${resp.data}"
            }
        }
    } catch (Exception e) {
        log.error "Exception in activateScene: ${e.message}"
    }
}

def deactivateScene() {
    if (!selector) {
        log.warn 'No selector specified. Please set the LIFX Selector in preferences.'
        return
    }

    log.debug 'Turning off lights associated with the scene'
    def authString = "${apiToken}:"
    def authEncoded = authString.bytes.encodeBase64().toString()
    def headers = ['Authorization': "Basic ${authEncoded}"]
    def body = [power: 'off']

    def params = [
        uri: 'https://api.lifx.com',
        path: "/v1/lights/${selector}/state",
        headers: headers,
        body: body,
        contentType: 'application/json'
    ]

    try {
        httpPut(params) { resp ->
            if (resp.status == 207 || resp.status == 200) {
                log.debug 'Lights turned off successfully'
                sendEvent(name: 'switch', value: 'off')
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
        def selectorString = null
        httpGet(params) { resp ->
            if (resp.status == 200) {
                def lights = resp.data.lights
                def selectors = lights.collect { it.id }
                selectorString = 'id:' + selectors.join(',')
                log.debug "Scene lights selector: ${selectorString}"
            } else {
                log.error "Error fetching scene details: HTTP ${resp.status} - ${resp.data}"
            }
        }
        return selectorString
    } catch (Exception e) {
        log.error "Exception in getSceneLightsSelector: ${e.message}"
        return null
    }
}

def activateMorphEffect() {
    if (!selector) {
        log.warn 'No selector specified. Please set the LIFX Selector in preferences.'
        return
    }

    log.debug 'Activating morph effect on lights associated with the scene'
    def authString = "${apiToken}:"
    def authEncoded = authString.bytes.encodeBase64().toString()
    def headers = ['Authorization': "Basic ${authEncoded}"]
    def body = [
        power_on: true,
        period: 5,
        palette: [
            'red',
            'green',
            'blue'
        ]
    ]

    def params = [
        uri: 'https://api.lifx.com',
        path: "/v1/lights/${selector}/effects/morph",
        headers: headers,
        body: body,
        contentType: 'application/json'
    ]

    try {
        httpPost(params) { resp ->
            if (resp.status == 207 || resp.status == 200) {
                log.debug 'Morph effect activated successfully'
            } else {
                log.error "Error activating morph effect: HTTP ${resp.status} - ${resp.data}"
            }
        }
    } catch (Exception e) {
        log.error "Exception in activateMorphEffect: ${e.message}"
    }
}

def effectsOff() {
    if (!selector) {
        log.warn 'No selector specified. Please set the LIFX Selector in preferences.'
        return
    }

    log.debug 'Turning off effects on lights associated with the scene'
    def authString = "${apiToken}:"
    def authEncoded = authString.bytes.encodeBase64().toString()
    def headers = ['Authorization': "Basic ${authEncoded}"]

    def params = [
        uri: 'https://api.lifx.com',
        path: "/v1/lights/${selector}/effects/off",
        headers: headers,
        contentType: 'application/json'
    ]

    try {
        httpPost(params) { resp ->
            if (resp.status == 207 || resp.status == 200) {
                log.debug 'Effects turned off successfully'
            } else {
                log.error "Error turning off effects: HTTP ${resp.status} - ${resp.data}"
            }
        }
    } catch (Exception e) {
        log.error "Exception in effectsOff: ${e.message}"
    }
}