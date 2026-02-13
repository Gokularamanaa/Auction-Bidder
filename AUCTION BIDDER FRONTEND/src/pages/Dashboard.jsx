import React, { useEffect, useState } from 'react';
import { Container, Typography, Box, Tabs, Tab, Grid, CircularProgress, Paper, Chip, Button } from '@mui/material';
import { useNavigate } from 'react-router-dom';
import AuctionCard from '../components/AuctionCard';
import axiosInstance from '../api/axiosInstance';
import EmojiEventsIcon from '@mui/icons-material/EmojiEvents';
import TrendingUpIcon from '@mui/icons-material/TrendingUp';
import TrendingDownIcon from '@mui/icons-material/TrendingDown';
import ExploreIcon from '@mui/icons-material/Explore';

function TabPanel(props) {
    const { children, value, index, ...other } = props;

    return (
        <div
            role="tabpanel"
            hidden={value !== index}
            id={`simple-tabpanel-${index}`}
            aria-labelledby={`simple-tab-${index}`}
            {...other}
        >
            {value === index && (
                <Box sx={{ py: 3 }}>
                    {children}
                </Box>
            )}
        </div>
    );
}

const SummaryCard = ({ title, value, color, icon, onClick }) => (
    <Paper
        elevation={0}
        onClick={onClick}
        sx={{
            height: '100%',
            bgcolor: 'background.paper',
            border: '1px solid',
            borderColor: 'divider',
            borderRadius: 4,
            cursor: 'pointer',
            transition: 'transform 0.2s',
            '&:hover': {
                transform: 'translateY(-4px)',
                borderColor: color,
                boxShadow: `0 10px 15px -3px ${color}20`
            }
        }}
    >
        <Box sx={{ p: 4 }}>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', mb: 2 }}>
                <Typography variant="body2" fontWeight="600" color="text.secondary" sx={{ textTransform: 'uppercase', letterSpacing: '0.05em' }}>
                    {title}
                </Typography>
                <Box sx={{
                    p: 1,
                    borderRadius: 2,
                    bgcolor: `${color}15`,
                    color: color,
                    display: 'flex'
                }}>
                    {icon}
                </Box>
            </Box>
            <Typography variant="h3" fontWeight="800" sx={{ color: 'text.primary', letterSpacing: '-0.02em' }}>
                {value}
            </Typography>
        </Box>
    </Paper>
);

export default function Dashboard() {
    const navigate = useNavigate();
    const [value, setValue] = useState(0);
    const [data, setData] = useState({ won: [], leading: [], trailing: [] });
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchData = async () => {
            try {
                const response = await axiosInstance.get('/dashboard');
                setData({
                    won: response.data.won || [],
                    leading: response.data.leading || [],
                    trailing: response.data.trailing || []
                });
            } catch (error) {
                console.error("Error fetching dashboard data", error);
                setError(error.message || "Failed to load dashboard");
            } finally {
                setLoading(false);
            }
        };
        fetchData();
    }, []);

    const handleChange = (event, newValue) => {
        setValue(newValue);
    };

    if (loading) return <Box sx={{ display: 'flex', justifyContent: 'center', mt: 10 }}><CircularProgress /></Box>;
    if (error) return <Container maxWidth="lg" sx={{ mt: 4 }}><Typography color="error">{error}</Typography></Container>;

    const EmptyState = ({ message }) => (
        <Box sx={{ textAlign: 'center', py: 6, bgcolor: 'background.paper', borderRadius: 4 }}>
            <Typography color="text.secondary" sx={{ mb: 3 }}>{message}</Typography>
            <Button
                variant="outlined"
                startIcon={<ExploreIcon />}
                onClick={() => navigate('/auctions')}
            >
                Explore Auctions
            </Button>
        </Box>
    );

    return (
        <Container maxWidth="lg">
            <Typography variant="h4" fontWeight="bold" gutterBottom sx={{ mb: 4, color: 'white' }}>
                Dashboard Overview
            </Typography>

            <Grid container spacing={3} sx={{ mb: 4 }}>
                <Grid item xs={12} md={4}>
                    <SummaryCard
                        title="Auctions Won"
                        count={data.won.length}
                        icon={<EmojiEventsIcon fontSize="large" />}
                        color="#10b981" // Emerald
                        active={value === 0}
                        onClick={() => setValue(0)}
                    />
                </Grid>
                <Grid item xs={12} md={4}>
                    <SummaryCard
                        title="Leading Bids"
                        count={data.leading.length}
                        icon={<TrendingUpIcon fontSize="large" />}
                        color="#3b82f6" // Blue
                        active={value === 1}
                        onClick={() => setValue(1)}
                    />
                </Grid>
                <Grid item xs={12} md={4}>
                    <SummaryCard
                        title="Trailing Bids"
                        count={data.trailing.length}
                        icon={<TrendingDownIcon fontSize="large" />}
                        color="#ef4444" // Red
                        active={value === 2}
                        onClick={() => setValue(2)}
                    />
                </Grid>
            </Grid>

            {/* Hidden Tabs for accessibility but driven by cards */}
            <Box sx={{ borderBottom: 1, borderColor: 'divider', display: 'none' }}>
                <Tabs value={value} onChange={handleChange}>
                    <Tab label="Won" />
                    <Tab label="Leading" />
                    <Tab label="Trailing" />
                </Tabs>
            </Box>

            <TabPanel value={value} index={0}>
                <Typography variant="h5" sx={{ mb: 3, fontWeight: 600, display: 'flex', alignItems: 'center', gap: 1 }}>
                    Won Auctions <Chip label={data.won.length} size="small" color="success" />
                </Typography>
                {data.won.length === 0 ? <EmptyState message="You haven't won any auctions yet." /> : (
                    <Grid container spacing={3}>
                        {data.won.map((auction) => (
                            <Grid item xs={12} sm={6} md={4} key={auction.auctionId}>
                                <AuctionCard auction={auction} />
                            </Grid>
                        ))}
                    </Grid>
                )}
            </TabPanel>

            <TabPanel value={value} index={1}>
                <Typography variant="h5" sx={{ mb: 3, fontWeight: 600, display: 'flex', alignItems: 'center', gap: 1 }}>
                    Currently Leading <Chip label={data.leading.length} size="small" color="primary" />
                </Typography>
                {data.leading.length === 0 ? <EmptyState message="You are not leading any auctions." /> : (
                    <Grid container spacing={3}>
                        {data.leading.map((auction) => (
                            <Grid item xs={12} sm={6} md={4} key={auction.auctionId}>
                                <AuctionCard auction={auction} />
                            </Grid>
                        ))}
                    </Grid>
                )}
            </TabPanel>

            <TabPanel value={value} index={2}>
                <Typography variant="h5" sx={{ mb: 3, fontWeight: 600, display: 'flex', alignItems: 'center', gap: 1 }}>
                    Trailing Bids <Chip label={data.trailing.length} size="small" color="error" />
                </Typography>
                {data.trailing.length === 0 ? <EmptyState message="You are not trailing in any auctions." /> : (
                    <Grid container spacing={3}>
                        {data.trailing.map((auction) => (
                            <Grid item xs={12} sm={6} md={4} key={auction.auctionId}>
                                <AuctionCard auction={auction} />
                            </Grid>
                        ))}
                    </Grid>
                )}
            </TabPanel>
        </Container>
    );
}
