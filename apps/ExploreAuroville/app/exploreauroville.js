const express = require('express'),
app = express(),
morgan = require('morgan');

const bearerToken = require('express-bearer-token');
app.use(bearerToken());

app.use(morgan('dev'));

var bodyParser = require('body-parser');
var multer = require('multer'); // v1.0.5
var upload = multer(); // for parsing multipart/form-data
var rawBodySaver = function (req, res, buf, encoding) {
  if (buf && buf.length) {
    req.rawBody = buf.toString(encoding || 'utf8');
  }
}
app.use(bodyParser.json({ verify: rawBodySaver }));
app.use(bodyParser.urlencoded({ verify: rawBodySaver, extended: true }));
app.use(bodyParser.raw({ verify: rawBodySaver, type: '*/*' }));

// 1.
app.get('/api/explorex/v1/location', function (req, res) {
    var locationJson = '[\
        {\
            "id": 2,\
            "name": "Future School",\
            "city_id": "1",\
            "latitude": "12.012354",\
            "longitude": "79.816276",\
            "address": "Transformatio, Auroville",\
            "zip": "605101",\
            "description": "Best School in AV",\
            "tags": "Future School, education, igcse, school"\
        },\
        {\
            "id": 3,\
            "name": "Town Hall",\
            "city_id": "1",\
            "latitude": "12.009636",\
            "longitude": "79.811019",\
            "address": "Auroville",\
            "zip": "605101",\
            "description": "Where bureaucracy lives...",\
            "tags": "administration, financial services, auroville radio, housing service"\
        },\
        {\
            "id": 4,\
            "name": "Matrimandir",\
            "city_id": "1",\
            "latitude": "12.006991",\
            "longitude": "79.810708",\
            "address": "Auroville",\
            "zip": "605101",\
            "description": "The golden egg...",\
            "tags": "meditation"\
        },\
        {\
            "id": 5,\
            "name": "Solar Kitchen",\
            "city_id": "1",\
            "latitude": "12.001513",\
            "longitude": "79.810354",\
            "address": "Auroville",\
            "zip": "605101",\
            "description": "Uses gas...",\
            "tags": "food, lunch, dinner, vegetarian"\
        }\
    ]';
    console.log(req.rawBody);
    res.send(locationJson );
});

// 2.
app.get('/api/explorex/v1/events', function (req, res) {
    var eventsJson = '[\
        {\
            "id": 2,\
            "name": "Live Jazz",\
            "location_id": 2,\
            "from_date": 1492965840283,\
            "to_date": 1492969440000,\
            "description": "Featuring bloo blah foo on drums!",\
            "tags": "music, jazz, food, drinks"\
        },\
        {\
            "id": 3,\
            "name": "Live R & B",\
            "location_id": 3,\
            "from_date": 1493965840283,\
            "to_date": 1493969440000,\
            "description": "Featuring bloo blah foo on piano!",\
            "tags": "music, jazz, food, drinks"\
        },\
        {\
            "id": 4,\
            "name": "Yoga",\
            "location_id": 4,\
            "from_date": 1492965840283,\
            "to_date": 1492969440000,\
            "description": "Stretch yourself...",\
            "tags": "yoga, exercise, health"\
        },\
        {\
            "id": 9,\
            "name": "Russian Bells",\
            "location_id": 5,\
            "from_date": 1494965840283,\
            "to_date": 1494969440000,\
            "description": "Featuring bloo blah foo on bells!",\
            "tags": "music, meditation, mellow"\
        }\
    ]';
    console.log(req.rawBody);
    res.send(eventsJson );
});

// 3.
app.post('/api/explorex/v1/phone', function (req, res) {
    console.log(req.rawBody);
    console.log(req.body);
    res.status(200).send("success" );
});

//4.
app.post('/api/explorex/v1/admin/login', function (req, res) {
    console.log(req.rawBody);
    res.setHeader("X-Token","eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MjcsInJvbGVfaWQiOiIzIiwiaWF0IjoxNDg3MjkxNTQzLCJleHAiOjE0ODcyOTI0NDN9.OtPYs5ByYcyKbuHIGpjV_PqmXRU9XYLml8_yIKrHR2A");
    let loginJson = {"superadmin": true};
    res.status(200).send(loginJson);
    // res.status(401).send("invalid token" );
});

// 5.
app.get('/api/explorex/v1/admin/events', function (req, res) {
    var eventsJson = '[\
        {\
            "id": 2,\
            "name": "Live Jazz",\
            "location_id": 2,\
            "from_date": 1492965840283,\
            "to_date": 1492969440000,\
            "description": "Featuring bloo blah foo on drums!",\
            "tags": "music, jazz, food, drinks"\
        },\
        {\
            "id": 3,\
            "name": "Live R & B",\
            "location_id": 2,\
            "from_date": 1493965840283,\
            "to_date": 1493969440000,\
            "description": "Featuring bloo blah foo on piano!",\
            "tags": "music, jazz, food, drinks"\
        },\
        {\
            "id": 4,\
            "name": "Yoga",\
            "location_id": 2,\
            "from_date": 1492965840283,\
            "to_date": 1492969440000,\
            "description": "Stretch yourself...",\
            "tags": "yoga, exercise, health"\
        },\
        {\
            "id": 9,\
            "name": "Russian Bells",\
            "location_id": 2,\
            "from_date": 1494965840283,\
            "to_date": 1494969440000,\
            "description": "Featuring bloo blah foo on bells!",\
            "tags": "music, meditation, mellow"\
        }\
    ]';
    console.log(req.rawBody);
    res.send(eventsJson );
});

// 6.
app.delete('/api/explorex/v1/admin/event', function (req, res) {
    console.log(req.rawBody);
    //console.log("Auth : " + req.get('Authorization'));
    res.status(200).send("success" );
});

// 6.
app.put('/api/explorex/v1/admin/event', function (req, res) {
    console.log(req.rawBody);
    //console.log("Auth : " + req.get('Authorization'));
    res.status(200).send("success" );
});

// 6.
app.post('/api/explorex/v1/admin/event', function (req, res) {
    console.log(req.rawBody);
    //console.log("Auth : " + req.get('Authorization'));
    res.status(200).send("success" );
});


app.listen(5678, function () {
    console.log('listening on port 5678!');
});