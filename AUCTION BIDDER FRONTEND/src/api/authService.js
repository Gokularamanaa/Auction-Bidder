import axiosInstance from './axiosInstance';

export const login = async (email, password) => {
  try {
    const response = await axiosInstance.post('/auth/login', { email, password });
    if (response.data.accessToken) {
      localStorage.setItem('token', response.data.accessToken);
      if (response.data.roles) {
        console.log("Login Success. Roles received:", response.data.roles);
        localStorage.setItem('roles', JSON.stringify(response.data.roles));
      }
    }
    return response.data;
  } catch (error) {
    throw error;
  }
};

export const register = async (name, email, password, role) => {
  try {
    const response = await axiosInstance.post('/auth/register', { name, email, password, role });
    return response.data;
  } catch (error) {
    throw error;
  }
};

export const logout = () => {
  localStorage.removeItem('token');
  localStorage.removeItem('roles');
  window.location.href = '/';
};
