import React, { useEffect, useState, useRef } from 'react';
import CryptoTable from '../components/CryptoTable';
import Portfolio from '../components/Portfolio';
import TransactionHistory from '../components/TransactionHistory';
import UserProfile from '../components/UserProfile';
import '../styles/form.css';
import '../styles/home-page.css';

const BATCH_SIZE = 50;
const MAX_SUBSCRIPTIONS = 20;
const KRAKEN_BASE_URI = process.env.REACT_APP_KRAKEN_BASE_API_URL;

const fetchAllUsdPairsWithVolume = async () => {
    try {
        const assetPairsRes = await fetch(
            `${KRAKEN_BASE_URI}/AssetPairs`
        );
        const assetPairsData = await assetPairsRes.json();
        const usdPairs = Object.entries(assetPairsData.result)
            .filter(([_, pair]) => pair.quote === 'ZUSD' && pair.wsname)
            .map(([key, pair]) => ({
                wsname: pair.wsname,
                base: pair.base,
                altname: pair.altname,
                key: key,
            }));

        let allTickerData = {};
        for (let i = 0; i < usdPairs.length; i += BATCH_SIZE) {
            const batch = usdPairs.slice(i, i + BATCH_SIZE);
            const batchKeys = batch.map((p) => p.key);
            try {
                const tickerRes = await fetch(
                    `${KRAKEN_BASE_URI}/Ticker?pair=${batchKeys.join(
                        ','
                    )}`
                );
                const tickerData = await tickerRes.json();
                Object.assign(allTickerData, tickerData.result);
            } catch (err) {
                for (const key of batchKeys) {
                    try {
                        const singleRes = await fetch(
                            `${KRAKEN_BASE_URI}/Ticker?pair=${key}`
                        );
                        const singleData = await singleRes.json();
                        Object.assign(allTickerData, singleData.result);
                    } catch (singleErr) {
                        console.error(
                            `Failed to fetch ticker for ${key}:`,
                            singleErr
                        );
                    }
                }
            }
        }

        const baseVolumes = {};
        usdPairs.forEach((pair) => {
            const ticker = allTickerData[pair.key];
            if (!ticker) return;
            const baseVolume = parseFloat(ticker.v ? ticker.v[0] : 0);
            const avgPrice = parseFloat(ticker.p ? ticker.p[1] : 0);
            const usdValue = baseVolume * avgPrice;
            if (!baseVolumes[pair.base]) {
                baseVolumes[pair.base] = [];
            }
            baseVolumes[pair.base].push({
                wsname: pair.wsname,
                base: pair.base,
                usdValue,
            });
        });

        const aggregated = Object.entries(baseVolumes).map(([base, pairs]) => {
            const totalUsdValue = pairs.reduce((sum, p) => sum + p.usdValue, 0);
            const bestPair = pairs.reduce((a, b) =>
                a.usdValue > b.usdValue ? a : b
            );
            return {
                base,
                wsname: bestPair.wsname,
                usdValue: totalUsdValue,
            };
        });

        return aggregated.sort((a, b) => b.usdValue - a.usdValue);
    } catch (err) {
        console.error('Error fetching top currencies:', err);
        return [];
    }
};

const HomePage = () => {
    const apiUrl = process.env.REACT_APP_BACKEND_BASE_API_URL;
    const [prices, setPrices] = useState({});
    const [topPairsInfo, setTopPairsInfo] = useState([]);
    const [user, setUser] = useState(null);
    const [transactions, setTransactions] = useState([]);
    const [tradeError, setTradeError] = useState(null);
    const [tradeInProgress, setTradeInProgress] = useState(false);
    const wsRef = useRef(null);
    const allPairsRef = useRef([]);
    const nextPairIndexRef = useRef(0);
    const activeSubscriptionsRef = useRef(new Set());

    const fetchUser = async () => {
        const token = localStorage.getItem('token');
        if (token) {
            try {
                const response = await fetch(`${apiUrl}/user`, {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                });
                if (response.ok) {
                    const userData = await response.json();
                    setUser(userData);
                }
            } catch (error) {
                console.error('Failed to fetch user data:', error);
            }
        }
    };

    const fetchTransactions = async () => {
        const token = localStorage.getItem('token');
        if (token) {
            try {
                const response = await fetch(`${apiUrl}/transaction/history`, {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                });
                if (response.ok) {
                    const transactionData = await response.json();
                    setTransactions(transactionData);
                }
            } catch (error) {
                console.error('Failed to fetch transaction data:', error);
            }
        }
    };

    const subscribeToNextAvailablePairs = (count) => {
        const ws = wsRef.current;
        if (!ws || ws.readyState !== WebSocket.OPEN) return;

        const pairsToSubscribe = [];
        while (
            pairsToSubscribe.length < count &&
            nextPairIndexRef.current < allPairsRef.current.length
        ) {
            const pair = allPairsRef.current[nextPairIndexRef.current];
            if (pair && !activeSubscriptionsRef.current.has(pair.wsname)) {
                pairsToSubscribe.push(pair);
            }
            nextPairIndexRef.current++;
        }

        if (pairsToSubscribe.length > 0) {
            const symbols = pairsToSubscribe.map((p) => p.wsname);
            ws.send(
                JSON.stringify({
                    method: 'subscribe',
                    params: { channel: 'ticker', symbol: symbols },
                })
            );
            console.log(`Attempting to subscribe to: ${symbols.join(', ')}`);
        }
    };

    useEffect(() => {
        fetchUser();
        fetchTransactions();

        let isMounted = true;

        const connect = () => {
            const ws = new WebSocket('wss://ws.kraken.com/v2');
            wsRef.current = ws;

            ws.onopen = () => {
                console.log('WebSocket connected');
                subscribeToNextAvailablePairs(MAX_SUBSCRIPTIONS);
            };

            ws.onmessage = (event) => {
                const data = JSON.parse(event.data);

                if (data.method === 'subscribe' && data.result && !data.error) {
                    const subscribedSymbol = data.result.symbol;
                    if (subscribedSymbol && !activeSubscriptionsRef.current.has(subscribedSymbol)) {
                        activeSubscriptionsRef.current.add(subscribedSymbol);
                        const pairInfo = allPairsRef.current.find(p => p.wsname === subscribedSymbol);
                        if (pairInfo) {
                            setTopPairsInfo(prevPairs => {
                                if (prevPairs.some(p => p.wsname === pairInfo.wsname)) {
                                    return prevPairs;
                                }
                                return [...prevPairs, pairInfo];
                            });
                        }
                    }
                } else if (
                    data.channel === 'ticker' &&
                    (data.type === 'snapshot' || data.type === 'update') &&
                    Array.isArray(data.data)
                ) {
                    setPrices((prev) => {
                        const updated = { ...prev };
                        data.data.forEach((item) => {
                            if (item.symbol && item.last) {
                                updated[item.symbol] = item.last;
                            }
                        });
                        return updated;
                    });
                } else if (data.error) {
                    const failedSymbol = data.result ? data.result.symbol : data.symbol;
                    if (failedSymbol) {
                        console.error(`Subscription error for ${failedSymbol}: ${data.error}`);
                        activeSubscriptionsRef.current.delete(failedSymbol);
                        setTopPairsInfo((prevPairs) =>
                            prevPairs.filter((p) => p.wsname !== failedSymbol)
                        );
                        if (activeSubscriptionsRef.current.size < MAX_SUBSCRIPTIONS) {
                            subscribeToNextAvailablePairs(1);
                        }
                    } else {
                        console.error('An unknown error occurred:', data);
                    }
                }
            };

            ws.onerror = (err) => {
                console.error('WebSocket error:', err);
            };

            ws.onclose = () => {
                console.log('WebSocket disconnected. Reconnecting...');
                if (isMounted) {
                    setTimeout(connect, 5000);
                }
            };
        };

        fetchAllUsdPairsWithVolume().then((allPairs) => {
            if (isMounted) {
                allPairsRef.current = allPairs;
                connect();
            }
        });

        return () => {
            isMounted = false;
            if (wsRef.current) {
                wsRef.current.close();
            }
            activeSubscriptionsRef.current.clear();
            setTopPairsInfo([]);
        };
    }, []);

    const handleTrade = async (type, pair, amount) => {
        const token = localStorage.getItem('token');
        if (!token) {
            setTradeError('You must be logged in to trade.');
            return;
        }

        if (!amount || amount <= 0) {
            setTradeError('Please enter a valid amount.');
            return;
        }

        setTradeInProgress(true);
        setTradeError(null);

        try {
            const response = await fetch(`${apiUrl}/transaction/${type}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    Authorization: `Bearer ${token}`,
                },
                body: JSON.stringify({
                    symbol: pair.wsname,
                    price: prices[pair.wsname],
                    amount: amount,
                }),
            });

            if (response.ok) {
                fetchUser();
                fetchTransactions();
            } else {
                const error = await response.json();
                setTradeError(error.message);
            }
        } catch (err) {
            setTradeError('An unexpected error occurred. Please try again.');
            console.error('Trade error:', err);
        } finally {
            setTradeInProgress(false);
        }
    };

    const handleWalletReset = async () => {
        const token = localStorage.getItem('token');
        if (!token) {
            setTradeError('You must be logged in to perform this action.');
            return;
        }

        try {
            const response = await fetch(`${apiUrl}/wallet/reset`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    Authorization: `Bearer ${token}`,
                },
            });

            if (response.ok) {
                fetchUser();
                fetchTransactions();
            } else {
                const error = await response.json();
                setTradeError(error.message);
            }
        } catch (err) {
            setTradeError('An unexpected error occurred. Please try again.');
            console.error('Wallet reset error:', err);
        }
    };

    return (
        <div className="form-bg">
            <div className="home-page-container">
                <div className="left-column">
                    <div className="home-page-card">
                        <h2 className="home-page-table-title">
                            Top 20 Crypto Prices (Live from Kraken)
                        </h2>
                        {tradeError && <p className="error-message">{tradeError}</p>}
                        {tradeInProgress && <p>Processing trade...</p>}
                        <CryptoTable
                            topPairsInfo={topPairsInfo}
                            prices={prices}
                            handleTrade={handleTrade}
                            tradeInProgress={tradeInProgress}
                        />
                    </div>
                </div>
                <div className="right-column">
                    <div className="info-card">
                        {user && <UserProfile user={user} onWalletReset={handleWalletReset} />}
                    </div>
                    <div className="info-card">
                        {user && <Portfolio user={user} />}
                    </div>
                    <div className="info-card">
                        <h2 className="home-page-table-title">Transaction History</h2>
                        <TransactionHistory transactions={transactions} />
                    </div>
                </div>
            </div>
        </div>
    );
};

export default HomePage;
