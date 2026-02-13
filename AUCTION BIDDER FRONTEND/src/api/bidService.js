import axiosInstance from './axiosInstance';

export const placeBid = async (auctionId, amount) => {
    try {
        // Backend expects query param 'amount' for POST /bids/{auctionId}
        const response = await axiosInstance.post(`/bids/${auctionId}?amount=${amount}`);
        return response.data;
    } catch (error) {
        throw error;
    }
};

export const getBidsForAuction = async (auctionId) => {
    try {
        const response = await axiosInstance.get(`/bids/${auctionId}`);
        return response.data;
    } catch (error) {
        throw error;
    }
};
