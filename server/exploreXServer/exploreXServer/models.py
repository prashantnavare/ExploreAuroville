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

class Admin(db.Model, CRUD):
    """
    Create a Admin table
    """
    __tablename__ = 'admins'
    id = db.Column(db.Integer, primary_key=True)
    eMail = db.Column(db.String(60), index=True, unique=True)
    userName = db.Column(db.String(60), index=True, unique=True)
    firstName = db.Column(db.String(60), index=True)
    lastName = db.Column(db.String(60), index=True)
    passwordHash = db.Column(db.String(128))
    isSuperAdmin = db.Column(db.Boolean, default=False)

    def __init__(self, eMail, userName, firstName, lastName, passwordHash, isSuperAdmin):
        self.eMail = eMail
        self.userName = userName
        self.firstName = firstName
        self.lastName = lastName
        self.passwordHash = passwordHash
        self.isSuperAdmin = isSuperAdmin

    @property
    def password(self):
        """
        Prevent pasword from being accessed
        """
        raise AttributeError('password is not a readable attribute.')

    @password.setter
    def password(self, password):
        """
        Set password to a hashed password
        """
        self.passwordHash = generate_password_hash(password)

    def verify_password(self, password):
        """
        Check if hashed password matches actual password
        """
        return check_password_hash(self.passwordHash, password)

    def __repr__(self):
        return '<User: {}>'.format(self.userName)

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

class Phone(db.Model, CRUD):
    """
    Create a Phone table
    """

    __tablename__ = 'phones'

    id = db.Column(db.Integer, primary_key=True)
    number = db.Column(db.String(15), unique=True)

    def __repr__(self):
        return '<Phone: {}>'.format(self.name)

    def __init__(self, number):
        self.number = number


