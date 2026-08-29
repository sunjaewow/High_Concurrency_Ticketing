import http from 'k6/http';
import {check} from 'k6';
import exec from 'k6/execution';

const CONCERT_ID = 2;

export const options = {
    scenarios: {
        measure: {
            executor: 'per-vu-iterations',
            vus: 1000,
            iterations: 1000,
        },
    },
    summaryTrendStats: ['avg', 'min', 'med', 'max', 'p(90)', 'p(95)', 'p(99)'],
};

export default function () {
    const userId = exec.scenario.iterationInTest + 1;
    const url = `http://localhost:8080/reservation-queues/${CONCERT_ID}/users/${userId}`;

    const response = http.post(url, null, {
        tags: {
            name: 'measure',
        },
    });

    check(response, {
        'queue entered': (res) => res.status === 201,
    });
}
