import http from 'k6/http';
import {check} from 'k6';

const CONCERT_ID = 1;
export const options = {
    scenarios: {
        measure: {
            executor: 'per-vu-iterations',
            vus: 50000,
            iterations: 1,
            maxDuration: '2m',
        },
    },
    summaryTrendStats: ['avg', 'min', 'med', 'max', 'p(90)', 'p(95)', 'p(99)'],
};

export default function () {
    const userId = __VU;
    const url = `http://localhost:8080/reservations/${CONCERT_ID}/users/${userId}`;

    const response = http.post(url, null, {
        tags: {
            name: 'measure',
        },
    });

    check(response, {
        'reservation created': (res) => res.status === 201,
    });
}
