import http from 'k6/http';
import {check, sleep} from 'k6';
import exec from 'k6/execution';

const CONCERT_ID = 1;
const NODE_INDEX = Number(__ENV.K6_NODE_INDEX || 0);
const USERS_PER_NODE = Number(__ENV.USERS_PER_NODE || 25000);
const START_AT = Number(__ENV.START_AT || 0);
const EC2_IP = String(__ENV.EC2_IP || 'localhost');

export const options = {
    scenarios: {
        measure: {
            executor: 'per-vu-iterations',
            vus: 25000,
            iterations: 1,
        },
    },
    summaryTrendStats: ['avg', 'min', 'med', 'max', 'p(90)', 'p(95)', 'p(99)'],
};

function waitUntilStartAt() {
    const waitMillis = START_AT - Date.now();
    if (waitMillis > 0) {
        sleep(waitMillis / 1000);
    }
}

export default function () {
    waitUntilStartAt();

    const userId = NODE_INDEX * USERS_PER_NODE + exec.scenario.iterationInTest + 1;
    const url = `http://${EC2_IP}:8080/reservation-queues/${CONCERT_ID}/users/${userId}`;

    const response = http.post(url, null, {
        tags: {
            name: 'measure',
            node: String(NODE_INDEX),
        },
    });

    check(response, {
        'queue entered': (res) => res.status === 201,
    });
}
