import React from 'react';
import { Card, CardContent, Typography, Button, Box, Stack } from '@mui/material';
import { useNavigate } from 'react-router-dom';
import StatusBadge from './StatusBadge';

const AuctionCard = ({ auction }) => {
  const navigate = useNavigate();

  return (
    <Card
      elevation={0}
      sx={{
        height: '100%',
        display: 'flex',
        flexDirection: 'column',
        bgcolor: 'background.paper',
        border: '1px solid',
        borderColor: 'divider',
        borderRadius: 4,
        transition: 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
        '&:hover': {
          transform: 'translateY(-8px)',
          boxShadow: '0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04)'
        }
      }}
    >
      <CardContent sx={{ flexGrow: 1, p: 3 }}>
        <Stack direction="row" justifyContent="space-between" alignItems="flex-start" mb={3}>
          <Typography variant="h6" fontWeight="bold" sx={{ lineHeight: 1.2, mb: 1, letterSpacing: '-0.02em' }}>
            {auction.title || `Auction #${auction.auctionId}`}
          </Typography>
          <StatusBadge status={auction.status} />
        </Stack>

        <Box sx={{ mb: 4 }}>
          <Typography variant="caption" color="text.secondary" sx={{ display: 'flex', alignItems: 'center', mb: 0.5, fontWeight: 600, letterSpacing: '0.05em' }}>
            CURRENT BID
          </Typography>
          <Typography variant="h4" color="primary.main" fontWeight="800" sx={{ letterSpacing: '-0.03em' }}>
            ₹{auction.currentHighBid || auction.startingPrice}
          </Typography>
        </Box>

        <Box sx={{ p: 2, bgcolor: 'background.default', borderRadius: 3, mb: 3 }}>
          <Stack direction="row" justifyContent="space-between" alignItems="center">
            <Typography variant="body2" color="text.secondary" fontWeight="500">My Bid</Typography>
            <Typography variant="body2" fontWeight="700">{auction.userBid ? `₹${auction.userBid}` : '-'}</Typography>
          </Stack>
          <Stack direction="row" justifyContent="space-between" alignItems="center" mt={1.5}>
            <Typography variant="body2" color="text.secondary" fontWeight="500">Ends In</Typography>
            <Typography variant="body2" fontWeight="700">{auction.endTime ? new Date(auction.endTime).toLocaleDateString() : 'N/A'}</Typography>
          </Stack>
        </Box>

        <Button
          fullWidth
          variant="contained"
          disableElevation
          sx={{
            py: 1.5,
            borderRadius: 3,
            textTransform: 'none',
            fontWeight: 700,
            fontSize: '1rem'
          }}
          onClick={() => navigate(`/auctions/${auction.auctionId}`)}
        >
          View Details
        </Button>
      </CardContent>
    </Card>
  );
};

export default AuctionCard;
