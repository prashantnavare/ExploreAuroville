from werkzeug.security import generate_password_hash, check_password_hash
from marshmallow_jsonapi import Schema, fields
from marshmallow import validate
from exploreXServer import db


#Class to add, update and delete data via SQLALchemy sessions
class CRUD():
    def add(self, resource):
        db.session.add(resource)
        return db.session.commit()

    def update(self):
        return db.session.commit()

    def delete(self, resource):
        db.session.delete(resource)
        return db.session.commit()

class CurrentEvent(db.Model, CRUD):
    """
    Create a CurrentEvent table
    """

    __tablename__ = 'currentevents'

    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(60), unique=True)
    from_date = db.Column(db.BigInteger)
    to_date = db.Column(db.BigInteger)
    description = db.Column(db.Text)
    tags = db.Column(db.Text)
    location_id = db.Column(db.Integer, db.ForeignKey('locations.id'))

    def __repr__(self):
        return '<CurrentEvent: {}>'.format(self.name)

    def __init__(self, name, from_date, to_date, description, tags, location_id):
        self.name = name
        self.from_date = from_date
        self.to_date = to_date
        self.description = description
        self.tags = tags
        self.location_id = location_id

class Location(db.Model, CRUD):
    """
    Create a Location table
    """

    __tablename__ = 'locations'

    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(60), unique=True)
    latitude = db.Column(db.String(60))
    longitude = db.Column(db.String(60))
    website = db.Column(db.String(60))
    description = db.Column(db.String(300))
    tags = db.Column(db.String(300))
    currentevents = db.relationship('CurrentEvent', backref='location', lazy='dynamic')

    def __repr__(self):
        return '<Location: {}>'.format(self.name)

    def __init__(self, name, latitude, longitude, website, description, tags):
        self.name = name
        self.latitude = latitude
        self.longitude = longitude
        self.website = website
        self.description = description
        self.tags = tags

class Feedback(db.Model, CRUD):
    """
    Create a feedback table
    """

    __tablename__ = 'feedback'

    id = db.Column(db.Integer, primary_key=True)
    feedback = db.Column(db.String(300))

    def __repr__(self):
        return '<Feedback: {}>'.format(self.name)

    def __init__(self, feedback):
        self.feedback = feedback


