from flask import Flask, Blueprint, request, jsonify, make_response, render_template
import csv
import time
import os
import io
from flask_restful import Api, Resource
from exploreXServer.models import db, CurrentEvent, Location, Feedback, Guest, GuestHistory
from sqlalchemy.exc import SQLAlchemyError
from sqlalchemy.orm.exc import NoResultFound
from sqlalchemy.orm.exc import MultipleResultsFound
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
                "location" : currentEvent.location,
                "accessLevel" : currentEvent.accessLevel
                            }
            jsonResults.append(currentEventData)
        return jsonResults

    def post(self):
        requestDict = request.get_json(force = True)
        try:
            newCurrentEvent = CurrentEvent(requestDict['name'], requestDict['from_date'], requestDict['to_date'], requestDict['description'], requestDict['tags'], requestDict['location'], requestDict['accessLevel']  )
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
                "tags" : location.tags,
                "accessLevel" : location.accessLevel
                            }
            jsonResults.append(locationData)
        return jsonResults

    def post(self):
        requestDict = request.get_json(force = True)
        try:
            newLocation = Location(requestDict['name'], requestDict['latitude'], requestDict['longitude'], requestDict['website'], requestDict['description'], requestDict['tags'], requestDict['accessLevel'] )
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


def purgeExpiredGuests():
    currentTime = int(round(time.time() * 1000))
    expiredGuestList = Guest.query.filter(Guest.to_date < currentTime).all()
    for expiredGuest in expiredGuestList:
        newGuestHistory = GuestHistory(expiredGuest.name, expiredGuest.phone, expiredGuest.from_date, expiredGuest.to_date, expiredGuest.sponsor, expiredGuest.location)
        newGuestHistory.add(newGuestHistory)
        expiredGuest.delete(expiredGuest)

class GuestAPI(Resource):

    def get(self):
        purgeExpiredGuests()
        phone = request.args.get('phone')
        if phone is not None:
            try:
                guest = Guest.query.filter(Guest.phone == phone).one()
                if guest is not None:
                    guestData = {
                        "id" : guest.id,
                        "name" : guest.name,
                        "phone" : guest.phone,
                        "from_date" : guest.from_date,
                        "to_date" : guest.to_date,
                        "sponsor" : guest.sponsor,
                        "location" : guest.location
                    }
                    return guestData, 200
            except NoResultFound as e:
                respData = jsonify({"error": str(e)})
                respData.status_code = 403
                return respData
            except MultipleResultsFound as e:
                respData = jsonify({"error": str(e)})
                respData.status_code = 403
                return respData
            except SQLAlchemyError as e:
                respData = jsonify({"error": str(e)})
                respData.status_code = 404
                return respData


        sponsor = request.args.get('sponsor')
        if sponsor is not None:
            guestList = Guest.query.filter(Guest.sponsor == sponsor).order_by(Guest.name).all()
            jsonResults = []
            for guest in guestList:
                guestData = {
                    "id" : guest.id,
                    "name" : guest.name,
                    "phone" : guest.phone,
                    "from_date" : guest.from_date,
                    "to_date" : guest.to_date,
                    "sponsor" : guest.sponsor,
                    "location" : guest.location
                }
                jsonResults.append(guestData)
            return jsonResults, 200

        

    def post(self):
        requestDict = request.get_json(force = True)
        try:
            newGuest = Guest(requestDict['name'], requestDict['phone'], requestDict['from_date'], requestDict['to_date'], requestDict['sponsor'], requestDict['location'])
            newGuest.add(newGuest)
            return newGuest.id, 201
        except SQLAlchemyError as e:
            db.session.rollback()
            respData = jsonify({"error": str(e)})
            respData.status_code = 403
            return respData

    def put(self):
        requestDict = request.get_json(force = True)
        try:
            guestID = requestDict['id']
            guestToBeUpdated = Guest.query.get_or_404(guestID)
            for key, value in requestDict.items():
                setattr(guestToBeUpdated, key, value)
            guestToBeUpdated.update()
            return  "success"
        except SQLAlchemyError as e:
            db.session.rollback()
            respData = jsonify({"error": str(e)})
            respData.status_code = 403
            return respData

    def delete(self):
        guestID = request.args.get('guestid')
        try:
            guestToBeDeleted = Guest.query.get_or_404(guestID)
            guestToBeDeleted.delete(guestToBeDeleted)
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

class PurgeEventAPI(Resource):

    def delete(self):
        cutoffTime = request.args.get('cutofftime', 0)
        try:
            numEvents = CurrentEvent.query.filter(CurrentEvent.from_date < cutoffTime).delete(synchronize_session='fetch')
            db.session.commit()
            print('Events Deleted = ', numEvents)
            return "success"
        except SQLAlchemyError as e:
            db.session.rollback()
            respData = jsonify({"error": str(e)})
            respData.status_code = 403
            return respData

adminApi.add_resource(CurrentEventAPI, '/event')
adminApi.add_resource(LocationAPI, '/location')
adminApi.add_resource(GuestAPI, '/guest')
adminApi.add_resource(FeedbackAPI, '/feedback')
adminApi.add_resource(PurgeEventAPI, '/purgeEvents')

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
            with io.open(file.filename, newline='') as csvfile:
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
            with io.open(file.filename, newline='') as csvfile:
                reader = csv.reader(csvfile)
                for row in reader:
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
                        newEvent = CurrentEvent(row[0], row[1], row[2], row[3], row[4], row[5])
                        newEvent.add(newEvent)

            
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
