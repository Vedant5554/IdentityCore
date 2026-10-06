import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    vus: 1,
    iterations: 1,
};

const BASE_URL = 'http://localhost:8082';
const PASSWORD = 'Passw0rd123';

export default function () {
    const vuId = __VU;
    const email = `loadtest-user-${vuId}@example.com`;

    const res = http.post(
        `${BASE_URL}/auth/login`,
        JSON.stringify({ email: email, password: PASSWORD }),
        { headers: { 'Content-Type': 'application/json' } }
    );

    console.log(`status: ${res.status}`);
    console.log(`body: ${res.body}`);

    check(res, {
        'status is 200': (r) => r.status === 200,
    });
}