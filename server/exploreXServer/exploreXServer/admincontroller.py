from flask import Flask, Blueprint, request, jsonify, make_response, render_template
import csv
import os
from flask_restful import Api, Resource
from exploreXServer.models import db, CurrentEvent, Location, Feedback
from sqlalchemy.exc import SQLAlchemyError
from marshmallow import ValidationError
from exploreXServer import app

import pymysql
pymysql.install_as_MySQLdb()

#Initialize a Flask Blueprint for the admin API
adminBluePrint = Blueprint('adminAPI', __name__)

#Initialize the admin API object using the Flask-RESTful API class
adminApi = Api(adminBluePrint)

class CurrentEventAPI(Resource):

    def get(self):
        location = request.args.get('location')
        fromDate = request.args.get('from', 0)
        toDate = request.args.get('to', 0xffffffffffffffff)
        if location is None: 
            currentEventList = CurrentEvent.query.filter(CurrentEvent.from_date >= fromDate).filter(CurrentEvent.from_date <= toDate).order_by(CurrentEvent.from_date).all()
        else:
            currentEventList = CurrentEvent.query.filter(CurrentEvent.location == location).filter(CurrentEvent.from_date >= fromDate).filter(CurrentEvent.from_date <= toDate).order_by(CurrentEvent.from_date).all()
        jsonResults = []
        for currentEvent in currentEventList:
            currentEventData = {
                "id" : currentEvent.id,
                "name" : currentEvent.name,
                "from_date" : currentEvent.from_date,
                "to_date" : currentEvent.to_date,
                "description" : currentEvent.description,
                "tags" : currentEvent.tags,
                "location" : currentEvent.location
                            }
            jsonResults.append(currentEventData)
        return jsonResults

    def post(self):
        requestDict = request.get_json(force = True)
        try:
            newCurrentEvent = CurrentEvent(requestDict['name'], requestDict['from_date'], requestDict['to_date'], requestDict['description'], requestDict['tags'], requestDict['location'] )
            newCurrentEvent.add(newCurrentEvent)
            return newCurrentEvent.id, 201
        except SQLAlchemyError as e:
            db.session.rollback()
            respData = jsonify({"error": str(e)})
            respData.status_code = 403
            return respData

    def put(self):
        requestDict = request.get_json(force = True)
        try:
            currentEventID = requestDict['id']
            currentEventToBeUpdated = CurrentEvent.query.get_or_404(currentEventID)
            for key, value in requestDict.items():
                setattr(currentEventToBeUpdated, key, value)
            currentEventToBeUpdated.update()
            return  "success"
        except SQLAlchemyError as e:
            db.session.rollback()
            respData = jsonify({"error": str(e)})
            respData.status_code = 403
            return respData

    def delete(self):
        eventID = request.args.get('eventid')
        try:
            eventToBeDeleted = CurrentEvent.query.get_or_404(eventID)
            eventToBeDeleted.delete(eventToBeDeleted)
            return "success"
        except SQLAlchemyError as e:
            db.session.rollback()
            respData = jsonify({"error": str(e)})
            respData.status_code = 403
            return respData

class LocationAPI(Resource):

    def get(self):
        locationList = Location.query.order_by(Location.name).all()
        jsonResults = []
        for location in locationList:
            locationData = {
                "id" : location.id,
                "name" : location.name,
                "latitude" : location.latitude,
                "longitude" : location.longitude,
                "website" : location.website,
                "description" : location.description,
                "tags" : location.tags
                            }
            jsonResults.append(locationData)
        return jsonResults

    def post(self):
        requestDict = request.get_json(force = True)
        try:
            newLocation = Location(requestDict['name'], requestDict['latitude'], requestDict['longitude'], requestDict['website'], requestDict['description'], requestDict['tags'] )
            newLocation.add(newLocation)
            return newLocation.id, 201
        except SQLAlchemyError as e:
            db.session.rollback()
            respData = jsonify({"error": str(e)})
            respData.status_code = 403
            return respData

    def put(self):
        requestDict = request.get_json(force = True)
        try:
            locationID = requestDict['id']
            locationToBeUpdated = Location.query.get_or_404(locationID)
            for key, value in requestDict.items():
                setattr(locationToBeUpdated, key, value)
            locationToBeUpdated.update()
            return  "success"
        except SQLAlchemyError as e:
            db.session.rollback()
            respData = jsonify({"error": str(e)})
            respData.status_code = 403
            return respData

    def delete(self):
        locationID = request.args.get('locationid')
        try:
            locationToBeDeleted = Location.query.get_or_404(locationID)
            locationToBeDeleted.delete(locationToBeDeleted)
            return "success"
        except SQLAlchemyError as e:
            db.session.rollback()
            respData = jsonify({"error": str(e)})
            respData.status_code = 403
            return respData



class FeedbackAPI(Resource):

    def get(self):
        feedbackList = Feedback.query.order_by(Feedback.id).all()
        jsonResults = []
        for feedback in feedbackList:
            feedbackData = {
                "id" : feedback.id,
                "feedback" : feedback.feedback,
                            }
            jsonResults.append(feedbackData)
        return jsonResults

    def post(self):
        requestDict = request.get_json(force = True)
        try:
            newFeedback = Feedback(requestDict['feedback'])
            newFeedback.add(newFeedback)
            return newFeedback.id, 201
        except SQLAlchemyError as e:
            db.session.rollback()
            respData = jsonify({"error": str(e)})
            respData.status_code = 403
            return respData

adminApi.add_resource(CurrentEventAPI, '/event')
adminApi.add_resource(LocationAPI, '/location')
adminApi.add_resource(FeedbackAPI, '/feedback')

app.config['ALLOWED_EXTENSIONS'] = set(['csv'])
# For a given file, return whether it's an allowed type or not
def allowed_file(filename):
    return '.' in filename and \
           filename.rsplit('.', 1)[1] in app.config['ALLOWED_EXTENSIONS']

@app.route('/api/explorex/v1/admin/uploadLocations', methods=['GET', 'POST'])
def upload_locations_file():
    if request.method == 'POST':
        # check if the post request has the file part
        if 'file' not in request.files:
            return 'No location file chosen'

        file = request.files['file']
        # if user does not select file, browser also
        # submit a empty part without filename
        if file.filename == '':
            return 'No selected locations file'

        if file and allowed_file(file.filename):
            file.save(file.filename)
            with open(file.filename, newline='') as csvfile:
                reader = csv.reader(csvfile)
                for row in reader:
                    locationName = row[0]
                    print(locationName)
                    existingLocation = Location.query.filter_by(name=locationName).first()
                    if existingLocation is not None:
                        print('existing location')
                        existingLocation.latitude       = row[1]
                        existingLocation.longitude      = row[2]
                        existingLocation.website        = row[3]
                        existingLocation.description    = row[4]
                        existingLocation.tags           = row[5]
                        existingLocation.update()

                    else:
                        print('new location')
                        newLocation = Location(row[0], row[1], row[2], row[3], row[4], row[5])
                        newLocation.add(newLocation)

                    print(row)
            
            os.remove(file.filename)
            return 'Location file uploaded successfully'

        return 'not a csv file'

    return '''
    <!doctype html>
    <title>Upload new Locations File</title>
    <h1>Upload new File</h1>
    <form method=post enctype=multipart/form-data>
      <p><input type=file name=file>
         <input type=submit value=Upload>
    </form>
    '''

@app.route('/api/explorex/v1/admin/uploadEvents', methods=['GET', 'POST'])
def upload_events_file():
    if request.method == 'POST':
        # check if the post request has the file part
        if 'file' not in request.files:
            return 'No events file chosen'

        file = request.files['file']
        # if user does not select file, browser also
        # submit a empty part without filename
        if file.filename == '':
            return 'No selected events file'

        if file and allowed_file(file.filename):
            file.save(file.filename)
            with open(file.filename, newline='') as csvfile:
                reader = csv.reader(csvfile)
                for row in reader:
                    print(row)
                    eventName = row[0]
                    startTime = row[1]
                    location = row[5]
                    existingEvent = CurrentEvent.query.filter_by(name=eventName).filter_by(from_date=startTime).filter_by(location=location).first()
                    if existingEvent is not None:
                        print('existing event')
                        existingEvent.from_date    = row[1]
                        existingEvent.to_date      = row[2]
                        existingEvent.description  = row[3]
                        existingEvent.tags         = row[4]
                        existingEvent.location     = row[5]
                        existingEvent.update()

                    else:
                        print('new event')
                        newEvent = Event(row[0], row[1], row[2], row[3], row[4], row[5])
                        newLocation.add(newLocation)

            
            os.remove(file.filename)
            return 'Event file uploaded successfully'

        return 'not a csv file'

    return '''
    <!doctype html>
    <title>Upload new File</title>
    <h1>Upload new Events File</h1>
    <form method=post enctype=multipart/form-data>
      <p><input type=file name=file>
         <input type=submit value=Upload>
    </form>
    '''
