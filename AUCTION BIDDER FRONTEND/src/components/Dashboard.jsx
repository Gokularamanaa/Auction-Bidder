// components/Dashboard.jsx
import AuctionCard from "./AuctionCard";

const Dashboard = ({ user, auctions }) => {
  const won = auctions.filter(a => a.status === "WON");
  const leading = auctions.filter(a => a.status === "LEADING");
  const trailing = auctions.filter(a => a.status === "TRAILING");

  return (
    <div className="p-6">
      <h1 className="text-2xl font-bold">Welcome, {user.name}</h1>

      <div className="grid grid-cols-3 gap-4 my-6">
        <div className="bg-green-100 p-4 rounded">
          <p className="text-xl font-bold">{won.length}</p>
          <p>Auctions Won</p>
        </div>
        <div className="bg-blue-100 p-4 rounded">
          <p className="text-xl font-bold">{leading.length}</p>
          <p>Currently Leading</p>
        </div>
        <div className="bg-red-100 p-4 rounded">
          <p className="text-xl font-bold">{trailing.length}</p>
          <p>Trailing Auctions</p>
        </div>
      </div>

      <h2 className="text-xl font-semibold mb-4">Your Auction History</h2>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        {auctions.map(auction => (
          <AuctionCard key={auction.id} auction={auction} />
        ))}
      </div>
    </div>
  );
};

export default Dashboard;
