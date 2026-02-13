import axiosInstance from './axiosInstance';

export const getAllAuctions = async () => {
    try {
        const response = await axiosInstance.get('/auctions');
        return response.data;
    } catch (error) {
        throw error;
    }
};

export const getAuctionById = async (id) => {
    try {
        const response = await axiosInstance.get(`/auctions/${id}`);
        return response.data;
    } catch (error) {
        throw error;
    }
};

export const createAuction = async (auctionData) => {
    try {
        const response = await axiosInstance.post('/auctions', auctionData);
        return response.data;
    } catch (error) {
        throw error;
    }
};
