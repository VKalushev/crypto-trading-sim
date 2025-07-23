import React, { useState } from 'react';
import './../styles/crypto-table.css';

const CryptoTable = ({ topPairsInfo, prices, handleTrade, tradeInProgress }) => {
    const [amount, setAmount] = useState({});

    const handleAmountChange = (e, symbol) => {
        setAmount({ ...amount, [symbol]: e.target.value });
    };

    const formatPrice = (price) => {
        const priceFloat = parseFloat(price);
        if (priceFloat < 1) {
            return priceFloat.toFixed(6);
        }
        return priceFloat.toFixed(2);
    };

    return (
        <table className="crypto-table">
            <thead>
                <tr>
                    <th>Rank</th>
                    <th>Symbol</th>
                    <th>Price (USD)</th>
                    <th>24h USD Volume</th>
                    <th>Amount</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                {topPairsInfo.map((pair, index) => (
                    <tr key={pair.wsname}>
                        <td>{index + 1}</td>
                        <td>{pair.wsname}</td>
                        <td>
                            {prices[pair.wsname]
                                ? formatPrice(prices[pair.wsname])
                                : 'Loading...'}
                        </td>
                        <td>
                            {pair.usdValue
                                ? pair.usdValue.toLocaleString(undefined, {
                                      maximumFractionDigits: 0,
                                  })
                                : 'Loading...'}
                        </td>
                        <td>
                            <input
                                type="number"
                                className="amount-input"
                                placeholder="Amount"
                                value={amount[pair.wsname] || ''}
                                onChange={(e) => handleAmountChange(e, pair.wsname)}
                                disabled={tradeInProgress}
                            />
                        </td>
                        <td>
                            <button
                                className="trade-btn buy"
                                onClick={() => handleTrade('buy', pair, amount[pair.wsname])}
                                disabled={tradeInProgress}
                            >
                                {tradeInProgress ? 'Buying...' : 'Buy'}
                            </button>
                            <button
                                className="trade-btn sell"
                                onClick={() => handleTrade('sell', pair, amount[pair.wsname])}
                                disabled={tradeInProgress}
                            >
                                {tradeInProgress ? 'Selling...' : 'Sell'}
                            </button>
                        </td>
                    </tr>
                ))}
            </tbody>
        </table>
    );
};

export default CryptoTable;
