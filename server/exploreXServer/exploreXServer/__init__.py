"""
The flask application package.
"""

from flask import Flask
from flask_sqlalchemy import SQLAlchemy
from flask_migrate import Migrate

#local imports
from config import app_config

app = Flask(__name__)
app.config.from_object(app_config['development'])

# db variable initialization
db = SQLAlchemy()
db.init_app(app)

migrate = Migrate(app, db)

from exploreXServer import models 
from exploreXServer import admincontroller 
from exploreXServer import views 

from admincontroller import adminBluePrint

app.register_blueprint(adminBluePrint, url_prefix='/api/explorex/v1/admin')
