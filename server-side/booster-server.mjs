import express from 'express';

const app = express();
const port = 8080;

app.use(express.json());

app.get('/cancelboostorder', (req, res) => {
  console.log('Triggered test api GET call')
  res.send({"message":"Boost order canceled successfully!"});
});

app.listen(port, () => {
  console.log(`Example app listening on port ${port}`);
});

app.post("/requestboostorder", (req, res) => {
	console.log('Triggered request boost order POST call!')
    	console.log(req.body)
	res.send(req.body);
});

