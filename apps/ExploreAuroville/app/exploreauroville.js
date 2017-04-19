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
app.get('/api/explorex/v1/poi', function (req, res) {
    var poiJson = '[\
        {\
            "id": 2,\
            "name": "Future School",\
            "location_id": "1",\
            "latitude": "12.012354",\
            "longitude": "79.816276",\
            "address": "Transformatio, Auroville",\
            "zip": "605101",\
            "tags": "Future School, education, igcse, school"\
        },\
        {\
            "id": 3,\
            "name": "Town Hall",\
            "location_id": "1",\
            "latitude": "12.009636",\
            "longitude": "79.811019",\
            "address": "Auroville",\
            "zip": "605101",\
            "tags": "administration, financial services, auroville radio, housing service"\
        },\
        {\
            "id": 4,\
            "name": "Matrimandir",\
            "location_id": "1",\
            "latitude": "12.006991",\
            "longitude": "79.810708",\
            "address": "Auroville",\
            "zip": "605101",\
            "tags": "meditation"\
        },\
        {\
            "id": 5,\
            "name": "Solar Kitchen",\
            "location_id": "1",\
            "latitude": "12.001513",\
            "longitude": "79.810354",\
            "address": "Auroville",\
            "zip": "605101",\
            "tags": "food, lunch, dinner, vegetarian"\
        }\
    ]';
    console.log(req.rawBody);
    res.send(poiJson );
});

app.listen(5678, function () {
    console.log('listening on port 5678!');
});