"""empty message

Revision ID: 9f59f2ada62d
Revises: 
Create Date: 2017-05-21 13:56:40.307000

"""
from alembic import op
import sqlalchemy as sa


# revision identifiers, used by Alembic.
revision = '9f59f2ada62d'
down_revision = None
branch_labels = None
depends_on = None


def upgrade():
    # ### commands auto generated by Alembic - please adjust! ###
    op.create_table('admins',
    sa.Column('id', sa.Integer(), nullable=False),
    sa.Column('eMail', sa.String(length=60), nullable=True),
    sa.Column('userName', sa.String(length=60), nullable=True),
    sa.Column('firstName', sa.String(length=60), nullable=True),
    sa.Column('lastName', sa.String(length=60), nullable=True),
    sa.Column('passwordHash', sa.String(length=128), nullable=True),
    sa.Column('isSuperAdmin', sa.Boolean(), nullable=True),
    sa.PrimaryKeyConstraint('id')
    )
    op.create_index(op.f('ix_admins_eMail'), 'admins', ['eMail'], unique=True)
    op.create_index(op.f('ix_admins_firstName'), 'admins', ['firstName'], unique=False)
    op.create_index(op.f('ix_admins_lastName'), 'admins', ['lastName'], unique=False)
    op.create_index(op.f('ix_admins_userName'), 'admins', ['userName'], unique=True)
    op.create_table('locations',
    sa.Column('id', sa.Integer(), nullable=False),
    sa.Column('name', sa.String(length=60), nullable=True),
    sa.Column('latitude', sa.String(length=60), nullable=True),
    sa.Column('longitude', sa.String(length=60), nullable=True),
    sa.Column('website', sa.String(length=60), nullable=True),
    sa.Column('description', sa.String(length=300), nullable=True),
    sa.Column('tags', sa.String(length=300), nullable=True),
    sa.PrimaryKeyConstraint('id'),
    sa.UniqueConstraint('name')
    )
    op.create_table('currentevents',
    sa.Column('id', sa.Integer(), nullable=False),
    sa.Column('name', sa.String(length=60), nullable=True),
    sa.Column('from_date', sa.String(length=64), nullable=True),
    sa.Column('to_date', sa.String(length=64), nullable=True),
    sa.Column('description', sa.Text(), nullable=True),
    sa.Column('tags', sa.Text(), nullable=True),
    sa.Column('location_id', sa.Integer(), nullable=True),
    sa.ForeignKeyConstraint(['location_id'], ['locations.id'], ),
    sa.PrimaryKeyConstraint('id'),
    sa.UniqueConstraint('name')
    )
    # ### end Alembic commands ###


def downgrade():
    # ### commands auto generated by Alembic - please adjust! ###
    op.drop_table('currentevents')
    op.drop_table('locations')
    op.drop_index(op.f('ix_admins_userName'), table_name='admins')
    op.drop_index(op.f('ix_admins_lastName'), table_name='admins')
    op.drop_index(op.f('ix_admins_firstName'), table_name='admins')
    op.drop_index(op.f('ix_admins_eMail'), table_name='admins')
    op.drop_table('admins')
    # ### end Alembic commands ###
