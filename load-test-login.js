import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    vus: 100,
    duration: '30s',

    thresholds: {
        // 99% of requests should complete below 200 ms
        http_req_duration: ['p(99)<200'],

        // At least 99% of login requests must return 200
        checks: ['rate>0.99'],
    },
};

const BASE_URL = 'http://localhost:8082';
const PASSWORD = 'Passw0rd123';

export default function () {
    // Each VU gets its own account.
    const vuId = __VU;

    const email = `loadtest-user-${vuId}@example.com`;

    const payload = JSON.stringify({
        email: email,
        password: PASSWORD,
    });

    const params = {
        headers: {
            'Content-Type': 'application/json',
        },
        tags: {
            endpoint: 'login',
        },
    };

    const res = http.post(
        `${BASE_URL}/auth/login`,
        payload,
        params
    );

    const success = check(res, {
        'status is 200': (r) => r.status === 200,
        'response is JSON': (r) =>
            r.headers['Content-Type']?.includes('application/json') ||
            r.headers['Content-Type']?.includes('application/problem+json'),
    });

    if (!success) {
        console.log(
            `FAILED | VU=${vuId} | status=${res.status} | email=${email} | body=${res.body}`
        );
    }

    sleep(0.1);
}