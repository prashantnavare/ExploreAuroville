from flask import Blueprint, request, jsonify, make_response
from flask_restful import Api, Resource
from models import db, CurrentEvent, Location
from sqlalchemy.exc import SQLAlchemyError
from marshmallow import ValidationError

#Initialize a Flask Blueprint for the admin API
adminBluePrint = Blueprint('adminAPI', __name__)

#Initialize the admin API object using the Flask-RESTful API class
adminApi = Api(adminBluePrint)

class CurrentEventAPI(Resource):

    def get(self):
        locationID = request.args.get('locationid', 0)
        fromDate = request.args.get('from', 0)
        toDate = request.args.get('to', 0)
        currentEventList = CurrentEvent.query.filter(CurrentEvent.location_id == locationID).filter(CurrentEvent.from_date >= fromDate).filter(CurrentEvent.from_date <= toDate).order_by(CurrentEvent.from_date).all()
        jsonResults = []
        for currentEvent in currentEventList:
            currentEventData = {
                "id" : currentEvent.id,
                "name" : currentEvent.name,
                "from_date" : currentEvent.from_date,
                "to_date" : currentEvent.to_date,
                "location_id" : currentEvent.location_id,
                "description" : currentEvent.description,
                "tags" : currentEvent.tags
                            }
            jsonResults.append(currentEventData)
        return jsonResults

    def post(self):
        requestDict = request.get_json(force = True)
        try:
            newCurrentEvent = CurrentEvent(requestDict['name'], requestDict['from_date'], requestDict['to_date'], requestDict['description'], requestDict['tags'], requestDict['location_id'] )
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

adminApi.add_resource(CurrentEventAPI, '/event')
adminApi.add_resource(LocationAPI, '/location')



