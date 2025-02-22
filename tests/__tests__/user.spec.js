import frisby from 'frisby';

frisby.baseUrl('http://localhost:8080');

const { Joi } = frisby;

const userSchema = Joi.object({
  id: Joi.number(),
  name: Joi.string(),
  email: Joi.string(),
});

describe('Users', () => {
  let user;

  const createUser = () => ({
    email: `${new Date().getTime()}@test.com`,
    name: 'testuser',
    password: 'password',
  });

  beforeAll(async () => {
    user = {
      email: `${new Date().getTime()}@test.com`,
      name: 'testuser',
      password: 'password',
    };
    const { json: userResponse } = await frisby.post('/users', user);
    user.id = userResponse.id;
    const { json } = await frisby.post('/session', {
      email: user.email,
      password: user.password,
    });

    const { accessToken } = json;

    frisby.globalSetup({
      request: {
        baseUrl: 'http://localhost:8080',
        headers: {
          Authorization: `Bearer ${accessToken}`,
        },
      },
    });
  });

  describe('POST /users', () => {
    context('with correct data', () => {
      it('responses user', async () => {
        await frisby.post('/users', createUser())
          .expect('status', 201)
          .expect('jsonTypes', userSchema);
      });
    });

    context('without required parameter', () => {
      it('responses 400 error', async () => {
        const promises = [
          { name: '' },
          { email: '' },
          { password: '' },
        ].map((it) => frisby.post('/users', {
          ...createUser(),
          ...it,
        }).expect('status', 400));

        await Promise.all(promises);
      });
    });
  });

  describe('PATCH /users/{id}', () => {
    const userData = {
      name: 'updated name',
      password: '12345678',
    };

    let id;

    context('with existing user', () => {
      beforeEach(async () => {
        id = user.id;
      });

      it('responses updated user', async () => {
        const { json } = await frisby.patch(`/users/${id}`, userData)
          .expect('status', 200);

        expect(json.name).toBe(userData.name);
      });
    });

    context('with others', () => {
      beforeEach(() => {
        id = 9999;
      });

      it('responses Forbidden', async () => {
        await frisby.patch(`/users/${id}`, userData)
          .expect('status', 403);
      });
    });

    context('with wrong parameter', () => {
      it('responses Bad Request', async () => {
        const promises = [
          { name: '' },
          { password: '' },
        ].map((it) => frisby.patch(`/users/${id}`, { ...userData, ...it })
          .expect('status', 400));

        await Promise.all(promises);
      });
    });
  });

  describe('DELETE /users/{id}', () => {
    let id;

    context('with existing user', () => {
      beforeEach(() => {
        id = user.id;
      });

      it('responses No Content', async () => {
        await frisby.del(`/users/${id}`)
          .expect('status', 204);
      });
    });

    context('with others', () => {
      beforeEach(() => {
        id = 9999;
      });

      it('responses Forbidden', async () => {
        await frisby.del(`/users/${id}`)
          .expect('status', 403);
      });
    });
  });
});
