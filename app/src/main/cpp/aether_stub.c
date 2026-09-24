#include <string.h>

static int g_running = 0;
static int g_ready = 0;
static void (*g_event_callback)(const char*) = NULL;
static int (*g_socket_protector)(int) = NULL;

int aether_prepare_json(const char* json) {
    (void)json;
    return 0;
}

const char* aether_last_result() {
    return "{\"ipv4\":\"172.16.0.2\",\"ipv6\":\"\",\"gateway_proxy\":\"\",\"organization\":\"MSN-GUARD\",\"token\":\"test_token\"}";
}

int aether_zt_request_email_code(const char* team, const char* email) {
    (void)team;
    (void)email;
    return 0;
}

int aether_zt_confirm_email_code(const char* code) {
    (void)code;
    return 0;
}

int aether_start_json_with_tun(const char* json, int tun_fd) {
    (void)json;
    (void)tun_fd;
    g_running = 1;
    g_ready = 1;
    if (g_event_callback) {
        g_event_callback("{\"type\":\"status\",\"status\":\"connected\"}");
    }
    return 0;
}

int aether_start_json(const char* json) {
    (void)json;
    g_running = 1;
    g_ready = 1;
    if (g_event_callback) {
        g_event_callback("{\"type\":\"status\",\"status\":\"connected\"}");
    }
    return 0;
}

int aether_stop() {
    g_running = 0;
    g_ready = 0;
    if (g_event_callback) {
        g_event_callback("{\"type\":\"status\",\"status\":\"disconnected\"}");
    }
    return 0;
}

int aether_is_running() {
    return g_running;
}

int aether_is_ready() {
    return g_ready;
}

const char* aether_last_error() {
    return "";
}

const char* aether_last_log() {
    return "";
}

void aether_set_socket_protector(int (*protector)(int)) {
    g_socket_protector = protector;
}

void aether_set_event_callback(void (*callback)(const char*)) {
    g_event_callback = callback;
}
