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
    name = db.Column(db.Text)
    from_date = db.Column(db.BigInteger)
    to_date = db.Column(db.BigInteger)
    description = db.Column(db.Text)
    tags = db.Column(db.Text)
    location = db.Column(db.Text)
    accessLevel = db.Column(db.Integer, default=3)

    def __repr__(self):
        return '<CurrentEvent: {}>'.format(self.name)

    def __init__(self, name, from_date, to_date, description, tags, location, accessLevel):
        self.name = name
        self.from_date = from_date
        self.to_date = to_date
        self.description = description
        self.tags = tags
        self.location = location
        self.accessLevel = accessLevel

class Location(db.Model, CRUD):
    """
    Create a Location table
    """

    __tablename__ = 'locations'

    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.Text)
    latitude = db.Column(db.String(30))
    longitude = db.Column(db.String(30))
    website = db.Column(db.Text)
    description = db.Column(db.Text)
    tags = db.Column(db.Text)
    accessLevel = db.Column(db.Integer, default=3)

    def __repr__(self):
        return '<Location: {}>'.format(self.name)

    def __init__(self, name, latitude, longitude, website, description, tags, accessLevel):
        self.name = name
        self.latitude = latitude
        self.longitude = longitude
        self.website = website
        self.description = description
        self.tags = tags
        self.accessLevel = accessLevel


class Guest(db.Model, CRUD):
    """
    Create a Guests table
    """

    __tablename__ = 'guests'

    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.Text)
    phone = db.Column(db.String(32), unique=True)
    from_date = db.Column(db.BigInteger)
    to_date = db.Column(db.BigInteger)
    sponsor = db.Column(db.Text)
    relationship = db.Column(db.Text)
    location = db.Column(db.Text)

    def __repr__(self):
        return '<Guest: {}>'.format(self.name)

    def __init__(self, name, phone, from_date, to_date, sponsor, relationship, location):
        self.name = name
        self.phone = phone
        self.from_date = from_date
        self.to_date = to_date
        self.sponsor = sponsor
        self.relationship = relationship
        self.location = location

class Feedback(db.Model, CRUD):
    """
    Create a feedback table
    """

    __tablename__ = 'feedback'

    id = db.Column(db.Integer, primary_key=True)
    feedback = db.Column(db.Text)

    def __repr__(self):
        return '<Feedback: {}>'.format(self.name)

    def __init__(self, feedback):
        self.feedback = feedback


